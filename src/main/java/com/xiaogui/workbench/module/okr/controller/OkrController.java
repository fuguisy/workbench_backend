package com.xiaogui.workbench.module.okr.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.common.Result;
import com.xiaogui.workbench.module.okr.entity.OkrObjective;
import com.xiaogui.workbench.module.okr.service.OkrService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/okr")
public class OkrController {

    @Resource
    private OkrService service;

    @GetMapping("/list")
    public Result<List<OkrObjective>> list() {
        return Result.ok(service.list(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/page")
    public Result<PageResult<OkrObjective>> page(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size) {
        return Result.ok(service.getPage(StpUtil.getLoginIdAsLong(), current, size));
    }

    @PostMapping("")
    public Result<Long> add(@RequestBody OkrObjective entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        service.add(entity);
        return Result.ok(entity.getId());
    }

    @PutMapping("")
    public Result<Void> update(@RequestBody OkrObjective entity) {
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
