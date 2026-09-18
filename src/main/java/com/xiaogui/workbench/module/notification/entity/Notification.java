package com.xiaogui.workbench.module.notification.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("notification")
public class Notification extends BaseEntity {

    private Long userId;
    private String title;
    private String content;
    private String type;

    // `read` 是 MySQL 保留字，必须用反引号包裹
    @TableField(value = "`read`", updateStrategy = FieldStrategy.IGNORED)
    private Boolean read;

    private String extra;
    private String priority;
    private String date;
}
