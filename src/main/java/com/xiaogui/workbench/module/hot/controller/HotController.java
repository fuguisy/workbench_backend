package com.xiaogui.workbench.module.hot.controller;

import com.xiaogui.workbench.common.Result;
import com.xiaogui.workbench.module.hot.service.HotPushService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 热点监控：手动触发接口（定时任务由 HotPushService 自动执行）
 */
@RestController
@RequestMapping("/hot")
public class HotController {

    @Resource
    private HotPushService hotPushService;

    /** 手动扫描一次热榜并推送命中的热点，返回推送条数 */
    @PostMapping("/scan")
    public Result<Integer> scan() {
        return Result.ok(hotPushService.scanAndPush());
    }
}
