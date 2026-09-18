package com.xiaogui.workbench.module.focus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaogui.workbench.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("focus_record")
public class FocusRecord extends BaseEntity {

    private Long userId;
    private String title;
    private String scene;
    private Integer minutesActual;
    private Integer minutesStandard;
    private Integer pomosExpected;
    private String status;
    private LocalDate recordDate;
    // 关联的待办任务ID（可选）
    private Long taskId;
}
