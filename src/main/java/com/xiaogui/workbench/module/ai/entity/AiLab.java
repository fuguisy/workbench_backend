package com.xiaogui.workbench.module.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaogui.workbench.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ai_lab")
public class AiLab extends BaseEntity {

    private Long userId;
    private String title;
    private String content;
    private String mood;
    private String date;
    private String category;
    private String model;
}
