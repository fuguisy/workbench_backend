package com.xiaogui.workbench.module.feishu;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.config.FeishuConfig;
import com.xiaogui.workbench.module.feishu.dto.FieldSchema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FeishuService {

    @Resource
    private FeishuConfig feishuConfig;

    private final TimedCache<String, String> tokenCache = CacheUtil.newTimedCache(7000 * 1000L);

    /**
     * 获取飞书 tenant_access_token，使用 Hutool 缓存
     */
    public String getTenantAccessToken() {
        String cached = tokenCache.get("tenant_access_token");
        if (StrUtil.isNotBlank(cached)) {
            return cached;
        }
        String url = feishuConfig.getBaseUrl() + "/open-apis/auth/v3/tenant_access_token/internal";
        Map<String, Object> body = new HashMap<>();
        body.put("app_id", feishuConfig.getAppId());
        body.put("app_secret", feishuConfig.getAppSecret());
        String resp = HttpRequest.of(url).method(Method.POST)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(body))
                .timeout(10000)
                .execute()
                .body();
        log.debug("获取飞书token响应: {}", resp);
        JSONObject json = JSONUtil.parseObj(resp);
        if (json.getInt("code", -1) != 0) {
            throw new BizException("获取飞书token失败: " + json.getStr("msg"));
        }
        String token = json.getStr("tenant_access_token");
        tokenCache.put("tenant_access_token", token);
        return token;
    }

    /**
     * 通用飞书API请求方法，自动携带 Authorization 头
     */
    public String request(String method, String path, Object body) {
        String url = feishuConfig.getBaseUrl() + path;
        HttpRequest httpRequest = HttpRequest.of(url)
                .method(Method.valueOf(method.toUpperCase()))
                .header("Authorization", "Bearer " + getTenantAccessToken())
                .header("Content-Type", "application/json")
                .timeout(15000);
        if (body != null) {
            httpRequest.body(JSONUtil.toJsonStr(body));
        }
        String resp = httpRequest.execute().body();
        log.debug("飞书API [{}] {} 响应: {}", method, path, resp);
        return resp;
    }

    /**
     * 创建多维表格，返回 appToken
     */
    public String createBase(String name) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        String resp = request("POST", "/open-apis/bitable/v1/apps", body);
        JSONObject json = JSONUtil.parseObj(resp);
        if (json.getInt("code", -1) != 0) {
            throw new BizException("创建多维表格失败: " + json.getStr("msg"));
        }
        return json.getJSONObject("data").getJSONObject("app").getStr("app_token");
    }

    /**
     * 在多维表格中创建数据表，返回 tableId
     */
    public String createTable(String appToken, String name, List<FieldSchema> fields) {
        Map<String, Object> table = new HashMap<>();
        table.put("name", name);
        table.put("fields", fields);
        Map<String, Object> body = new HashMap<>();
        body.put("table", table);
        String resp = request("POST", "/open-apis/bitable/v1/apps/" + appToken + "/tables", body);
        JSONObject json = JSONUtil.parseObj(resp);
        if (json.getInt("code", -1) != 0) {
            throw new BizException("创建数据表失败: " + json.getStr("msg"));
        }
        return json.getJSONObject("data").getJSONObject("table").getStr("table_id");
    }

    /**
     * 批量写入记录到数据表
     */
    public void batchInsert(String appToken, String tableId, List<Map<String, Object>> records) {
        List<Map<String, Object>> recordList = records.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("fields", r);
            return m;
        }).collect(Collectors.toList());
        Map<String, Object> body = new HashMap<>();
        body.put("records", recordList);
        String resp = request("POST",
                "/open-apis/bitable/v1/apps/" + appToken + "/tables/" + tableId + "/records/batch_create",
                body);
        JSONObject json = JSONUtil.parseObj(resp);
        if (json.getInt("code", -1) != 0) {
            throw new BizException("批量写入记录失败: " + json.getStr("msg"));
        }
    }
}
