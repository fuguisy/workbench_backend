package com.xiaogui.workbench.module.checkin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.common.Result;
import com.xiaogui.workbench.module.checkin.entity.Checkin;
import com.xiaogui.workbench.module.checkin.service.CheckinRecordService;
import com.xiaogui.workbench.module.checkin.service.CheckinService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/checkin")
public class CheckinController {

    @Resource
    private CheckinService service;

    @Resource
    private CheckinRecordService recordService;

    @GetMapping("/list")
    public Result<List<Checkin>> list() {
        return Result.ok(service.list(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/page")
    public Result<PageResult<Checkin>> page(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size) {
        return Result.ok(service.getPage(StpUtil.getLoginIdAsLong(), current, size));
    }

    @PostMapping("")
    public Result<Void> add(@RequestBody Checkin entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        service.add(entity);
        return Result.ok();
    }

    @PutMapping("")
    public Result<Void> update(@RequestBody Checkin entity) {
        service.update(entity);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        recordService.deleteByHabit(id);
        return Result.ok();
    }

    // ====== 打卡面板接口 ======

    /** 今日打卡/取消打卡（toggle），返回 true=已打卡 false=已取消 */
    @PostMapping("/punch/{habitId}")
    public Result<Boolean> punch(@PathVariable Long habitId) {
        return Result.ok(recordService.punch(StpUtil.getLoginIdAsLong(), habitId));
    }

    /**
     * 打卡面板聚合数据：习惯列表 + 今日状态 + 连续天数 + 顶部统计。
     */
    @GetMapping("/panel")
    public Result<Map<String, Object>> panel() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Checkin> habits = service.list(userId);
        List<Long> habitIds = habits.stream().map(Checkin::getId).collect(Collectors.toList());
        Map<Long, CheckinRecordService.HabitStatus> statusMap = recordService.batchStatus(userId, habitIds);
        CheckinRecordService.PanelStats stats = recordService.stats(userId, habitIds);

        List<Map<String, Object>> habitList = habits.stream().map(h -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", h.getId());
            m.put("title", h.getTitle());
            m.put("groupName", h.getGroupName());
            m.put("cycle", h.getCycle());
            m.put("log", h.getLog());
            m.put("createTime", h.getCreateTime());
            m.put("updateTime", h.getUpdateTime());
            CheckinRecordService.HabitStatus st = statusMap.get(h.getId());
            if (st != null) {
                m.put("todayDone", st.todayDone);
                m.put("streak", st.streak);
                m.put("totalCount", st.totalCount);
            } else {
                m.put("todayDone", false);
                m.put("streak", 0);
                m.put("totalCount", 0);
            }
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("habits", habitList);
        result.put("stats", stats);
        return Result.ok(result);
    }

    /** 某习惯某月打卡日期列表（格式 yyyy-MM-dd） */
    @GetMapping("/month-dates/{habitId}")
    public Result<List<String>> monthDates(@PathVariable Long habitId,
                                           @RequestParam int year,
                                           @RequestParam int month) {
        return Result.ok(recordService.monthDates(StpUtil.getLoginIdAsLong(), habitId, year, month));
    }
}
