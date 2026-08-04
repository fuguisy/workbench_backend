package com.xiaogui.workbench.module.project.entity;

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
@TableName("project")
public class Project extends BaseEntity {

    private Long userId;
    private String title;
    private Integer current;
    private Integer target;
    private String unit;
    private String note;
    private String status;
    private String deadline;
    private String category;
    private String owner;
}
