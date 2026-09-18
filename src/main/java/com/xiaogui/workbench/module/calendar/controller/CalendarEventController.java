package com.xiaogui.workbench.module.calendar.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.common.Result;
import com.xiaogui.workbench.module.calendar.entity.CalendarEvent;
import com.xiaogui.workbench.module.calendar.service.CalendarEventService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calendar")
public class CalendarEventController {

    @Resource
    private CalendarEventService service;

    @GetMapping("/list")
    public Result<List<CalendarEvent>> list() {
        return Result.ok(service.list(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/page")
    public Result<PageResult<CalendarEvent>> page(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size) {
        return Result.ok(service.getPage(StpUtil.getLoginIdAsLong(), current, size));
    }

    @PostMapping("")
    public Result<Void> add(@RequestBody CalendarEvent entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        service.add(entity);
        return Result.ok();
    }

    @PutMapping("")
    public Result<Void> update(@RequestBody CalendarEvent entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        service.update(entity);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id, StpUtil.getLoginIdAsLong());
        return Result.ok();
    }
}
