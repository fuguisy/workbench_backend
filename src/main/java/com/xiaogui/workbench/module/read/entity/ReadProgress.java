package com.xiaogui.workbench.module.read.entity;

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
@TableName("read_progress")
public class ReadProgress extends BaseEntity {

    private Long userId;
    private String title;
    private Integer current;
    private Integer target;
    private String unit;
    private String note;
}
