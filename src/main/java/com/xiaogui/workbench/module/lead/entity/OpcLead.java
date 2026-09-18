package com.xiaogui.workbench.module.lead.entity;

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
@TableName("opc_lead")
public class OpcLead extends BaseEntity {

    private Long userId;
    private String title;
    private String source;
    private String stage;
    private String contact;
    private BigDecimal budget;
    private BigDecimal quote;
    private String nextFollow;
    private String demand;
    private String note;
}
