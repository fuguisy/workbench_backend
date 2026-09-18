package com.xiaogui.workbench.module.subscription.entity;

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
@TableName("subscription")
public class Subscription extends BaseEntity {

    private Long userId;
    private String title;
    private String category;
    private String provider;
    private String planType;
    private BigDecimal costYuan;
    private LocalDate renewDate;
    private Boolean autoRenew;
    private String status;
}
