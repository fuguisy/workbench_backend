package com.xiaogui.workbench.module.feishu;

import com.xiaogui.workbench.common.Result;
import com.xiaogui.workbench.module.feishu.dto.CreateTableDTO;
import com.xiaogui.workbench.module.feishu.dto.FeishuSyncDTO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/feishu")
public class FeishuController {

    @Resource
    private FeishuService feishuService;

    @PostMapping("/test")
    public Result<String> test() {
        String token = feishuService.getTenantAccessToken();
        return Result.ok("飞书连接成功", token);
    }

    @PostMapping("/base/create")
    public Result<String> createBase(@RequestBody Map<String, String> body) {
        String name = body.getOrDefault("name", "晓贵工作台数据");
        return Result.ok(feishuService.createBase(name));
    }

    @PostMapping("/table/create")
    public Result<String> createTable(@RequestBody CreateTableDTO dto) {
        return Result.ok(feishuService.createTable(dto.getAppToken(), dto.getName(), dto.getFields()));
    }

    @PostMapping("/data/sync")
    public Result<Void> sync(@RequestBody FeishuSyncDTO dto) {
        feishuService.batchInsert(dto.getAppToken(), dto.getTableId(), dto.getRecords());
        return Result.ok();
    }
}
