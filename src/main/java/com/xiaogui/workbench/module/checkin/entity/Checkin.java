package com.xiaogui.workbench.module.checkin.entity;

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
@TableName("checkin")
public class Checkin extends BaseEntity {

    private Long userId;
    private String title;
    private String log;
    private String groupName;
    private String cycle;
}
