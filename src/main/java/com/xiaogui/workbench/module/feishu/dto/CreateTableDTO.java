package com.xiaogui.workbench.module.feishu.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateTableDTO {
    private String appToken;
    private String name;
    private List<FieldSchema> fields;
}
