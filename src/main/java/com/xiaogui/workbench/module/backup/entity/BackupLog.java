package com.xiaogui.workbench.module.backup.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaogui.workbench.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("backup_log")
public class BackupLog extends BaseEntity {

    private Long userId;
    private String title;
    private String content;
    private String mood;
    private String date;
    private String category;
    private String destination;
    private BigDecimal sizeGb;
}
