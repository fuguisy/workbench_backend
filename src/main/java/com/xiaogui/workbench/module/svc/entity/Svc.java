package com.xiaogui.workbench.module.svc.entity;

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
@TableName("svc")
public class Svc extends BaseEntity {

    private Long userId;
    private String title;
    private String content;
    private String mood;
    private String date;
    private String host;
    private String port;
    private String deploy;
}
