package com.xiaogui.workbench.module.okr.entity;

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
@TableName("okr_key_result")
public class OkrKeyResult extends BaseEntity {

    private Long userId;
    /** 所属O目标ID */
    private Long objectiveId;
    /** KR标题 */
    private String title;
    /** 当前值 */
    private BigDecimal currValue;
    /** 目标值 */
    private BigDecimal targetValue;
    /** 单位 */
    private String unit;
    /** 进度百分比 0-100 */
    private Integer progress;
    /** 进度模式：MANUAL|TASK_LINKED */
    private String progressMode;
    /** 关联任务ID（progress_mode=TASK_LINKED时） */
    private Long taskId;
    /** 权重（默认1，用于O进度加权计算） */
    private Integer weight;
    /** 状态：ACTIVE|DONE|CANCEL */
    private String status;
}
