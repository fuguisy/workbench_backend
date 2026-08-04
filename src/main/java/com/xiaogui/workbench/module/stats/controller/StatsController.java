package com.xiaogui.workbench.module.stats.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaogui.workbench.common.Result;
import com.xiaogui.workbench.module.stats.dto.DashboardStatsVO;
import com.xiaogui.workbench.module.stats.dto.InsightStatsVO;
import com.xiaogui.workbench.module.stats.service.StatsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatsController {

    @Resource
    private StatsService statsService;

    @GetMapping("/dashboard")
    public Result<DashboardStatsVO> dashboard() {
        return Result.ok(statsService.dashboard(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/insight")
    public Result<InsightStatsVO> insight() {
        return Result.ok(statsService.insight(StpUtil.getLoginIdAsLong()));
    }
}
