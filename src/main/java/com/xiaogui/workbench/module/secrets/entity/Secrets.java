package com.xiaogui.workbench.module.secrets.entity;

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
@TableName("secrets")
public class Secrets extends BaseEntity {

    private Long userId;
    private String title;
    private String content;
    private String mood;
    private String date;
    private String category;
    private String location;
}
