package com.xiaogui.workbench.module.feishu.dto;

import lombok.Data;

@Data
public class FieldSchema {

    private String fieldName;
    private Integer type;

    public FieldSchema() {
    }

    public FieldSchema(String fieldName, Integer type) {
        this.fieldName = fieldName;
        this.type = type;
    }
}
