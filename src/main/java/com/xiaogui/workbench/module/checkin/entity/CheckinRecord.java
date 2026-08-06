package com.xiaogui.workbench.module.checkin.entity;

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
@TableName("checkin_record")
public class CheckinRecord extends BaseEntity {

    private Long userId;

    /** 习惯id，关联 checkin.id */
    private Long habitId;

    /** 打卡日期 */
    private LocalDate checkDate;

    /** 打卡备注 */
    private String note;
}
