package com.xiaogui.workbench.module.okr.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaogui.workbench.common.Result;
import com.xiaogui.workbench.module.okr.entity.OkrKeyResult;
import com.xiaogui.workbench.module.okr.service.OkrKeyResultService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/okr/kr")
public class OkrKeyResultController {

    @Resource
    private OkrKeyResultService service;

    /** 查询某O目标下的全部KR */
    @GetMapping("/list/{objectiveId}")
    public Result<List<OkrKeyResult>> list(@PathVariable Long objectiveId) {
        return Result.ok(service.listByObjective(objectiveId, StpUtil.getLoginIdAsLong()));
    }

    @PostMapping("")
    public Result<Long> add(@RequestBody OkrKeyResult entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        service.add(entity);
        return Result.ok(entity.getId());
    }

    @PutMapping("")
    public Result<Void> update(@RequestBody OkrKeyResult entity) {
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
