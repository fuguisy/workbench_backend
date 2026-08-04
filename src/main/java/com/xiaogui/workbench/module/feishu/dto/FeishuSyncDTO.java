package com.xiaogui.workbench.module.feishu.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FeishuSyncDTO {

    private String moduleKey;
    private String appToken;
    private String tableId;
    private List<Map<String, Object>> records;
}
