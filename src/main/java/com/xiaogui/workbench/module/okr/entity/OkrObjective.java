package com.xiaogui.workbench.module.okr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaogui.workbench.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("okr_objective")
public class OkrObjective extends BaseEntity {

    private Long userId;
    private String title;
    private String category;
    private String period;
    private BigDecimal currValue;
    private BigDecimal targetValue;
    private String unit;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer progress;
    /** 周期结束0-1打分 */
    private BigDecimal score;
    /** 复盘备注 */
    private String review;
}
