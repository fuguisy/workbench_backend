package com.xiaogui.workbench.module.hot.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaogui.workbench.module.auth.entity.SysUser;
import com.xiaogui.workbench.module.auth.mapper.SysUserMapper;
import com.xiaogui.workbench.module.notification.entity.Notification;
import com.xiaogui.workbench.module.notification.service.NotificationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * OPC 热点监控推送：定时抓取微博/知乎热榜，命中关键词后写入通知中心（type=HOT）。
 * 配置项（application.yml 可覆盖）：
 *   opc.hot.keywords  关键词，逗号分隔
 *   opc.hot.cron      扫描频率，默认每天 8/12/20 点
 *   opc.hot.max-push  单次最多推送条数，默认 5
 */
@Service
@Slf4j
public class HotPushService {

    @Resource
    private NotificationService notificationService;
    @Resource
    private SysUserMapper sysUserMapper;

    @Value("${opc.hot.keywords:AI,大模型,智能体,Agent,AIGC,GPT,Claude,DeepSeek,OpenAI,Spring AI,程序员,副业,一人公司,自动化}")
    private String keywordsConfig;

    @Value("${opc.hot.max-push:5}")
    private int maxPush;

    private static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0 Safari/537.36";

    private record HotItem(String source, String title, String url) {}

    @Scheduled(cron = "${opc.hot.cron:0 0 8,12,20 * * ?}")
    public void scheduledScan() {
        int n = scanAndPush();
        if (n > 0) {
            log.info("[热点监控] 本次推送 {} 条", n);
        }
    }

    /** 扫描热榜并按关键词过滤、去重后推送给所有用户，返回推送条数 */
    public int scanAndPush() {
        List<String> keywords = StrUtil.split(keywordsConfig, ',', true, true);
        List<HotItem> items = new ArrayList<>();
        items.addAll(fetchWeibo());
        items.addAll(fetchZhihu());

        String today = LocalDate.now().toString();
        List<SysUser> users = sysUserMapper.selectList(null);
        int pushed = 0;
        for (HotItem item : items) {
            if (pushed >= maxPush) {
                break;
            }
            if (StrUtil.isBlank(item.title()) || !matches(item.title(), keywords)) {
                continue;
            }
            for (SysUser user : users) {
                if (existsToday(user.getId(), item.title(), today)) {
                    continue;
                }
                Notification n = new Notification();
                n.setUserId(user.getId());
                n.setTitle("热点：" + item.title());
                n.setContent(item.url());
                n.setType("HOT");
                n.setRead(false);
                n.setExtra(item.source());
                n.setPriority("中");
                n.setDate(today);
                notificationService.add(n);
            }
            pushed++;
        }
        return pushed;
    }

    private boolean matches(String title, List<String> keywords) {
        for (String kw : keywords) {
            if (StrUtil.containsIgnoreCase(title, kw)) {
                return true;
            }
        }
        return false;
    }

    private boolean existsToday(Long userId, String title, String today) {
        return notificationService.count(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getType, "HOT")
                .eq(Notification::getTitle, "热点：" + title)
                .eq(Notification::getDate, today)) > 0;
    }

    /** 微博热搜（公开接口，无需登录） */
    private List<HotItem> fetchWeibo() {
        List<HotItem> list = new ArrayList<>();
        try {
            String body = HttpRequest.get("https://weibo.com/ajax/side/hotSearch")
                    .header("User-Agent", UA).timeout(5000).execute().body();
            JSONArray realtime = JSONUtil.parseObj(body).getJSONObject("data").getJSONArray("realtime");
            for (int i = 0; i < realtime.size(); i++) {
                JSONObject it = realtime.getJSONObject(i);
                String word = it.getStr("word", it.getStr("note", ""));
                String url = "https://s.weibo.com/weibo?q=" + URLEncoder.encode(word, StandardCharsets.UTF_8);
                list.add(new HotItem("微博热搜", it.getStr("note", word), url));
            }
        } catch (Exception e) {
            log.warn("[热点监控] 微博热搜抓取失败：{}", e.getMessage());
        }
        return list;
    }

    /** 知乎热榜（公开接口，需要 UA） */
    private List<HotItem> fetchZhihu() {
        List<HotItem> list = new ArrayList<>();
        try {
            String body = HttpRequest.get("https://www.zhihu.com/api/v3/feed/topstory/hot-lists/total?limit=30")
                    .header("User-Agent", UA).timeout(5000).execute().body();
            JSONArray data = JSONUtil.parseObj(body).getJSONArray("data");
            for (int i = 0; i < data.size(); i++) {
                JSONObject target = data.getJSONObject(i).getJSONObject("target");
                list.add(new HotItem("知乎热榜", target.getStr("title", ""),
                        "https://www.zhihu.com/question/" + target.getLong("id", 0L)));
            }
        } catch (Exception e) {
            log.warn("[热点监控] 知乎热榜抓取失败：{}", e.getMessage());
        }
        return list;
    }
}
