package com.xiaogui.workbench.module.lead.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.common.Result;
import com.xiaogui.workbench.module.lead.entity.OpcLead;
import com.xiaogui.workbench.module.lead.service.OpcLeadService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lead")
public class OpcLeadController {

    @Resource
    private OpcLeadService service;

    @GetMapping("/list")
    public Result<List<OpcLead>> list() {
        return Result.ok(service.list(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/page")
    public Result<PageResult<OpcLead>> page(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size) {
        return Result.ok(service.getPage(StpUtil.getLoginIdAsLong(), current, size));
    }

    @PostMapping("")
    public Result<Void> add(@RequestBody OpcLead entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        service.add(entity);
        return Result.ok();
    }

    @PutMapping("")
    public Result<Void> update(@RequestBody OpcLead entity) {
        service.update(entity);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }
}
