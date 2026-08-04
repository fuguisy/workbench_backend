package com.xiaogui.workbench.module.media.entity;

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
@TableName("media")
public class Media extends BaseEntity {

    private Long userId;
    private String title;
    private Integer current;
    private Integer target;
    private String unit;
    private String note;
    private String stage;
    private String platform;
    private String date;
    private String url;
    private Integer views;
}
