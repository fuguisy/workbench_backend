package com.xiaogui.workbench.module.money.entity;

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
@TableName("money")
public class Money extends BaseEntity {

    private Long userId;
    private String title;
    private String type;
    private BigDecimal amount;
    private String category;
    private String date;
    private String source;
    private String budget;
}
