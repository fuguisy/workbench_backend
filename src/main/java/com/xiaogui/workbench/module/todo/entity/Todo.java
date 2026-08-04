package com.xiaogui.workbench.module.todo.entity;

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
@TableName("todo")
public class Todo extends BaseEntity {

    private Long userId;
    private String title;
    private String priority;
    private Integer done;
    private String note;
    private String category;
    private String deadline;
    private String recurring;
    private Integer estPom;
    private Integer pomoMinutes;
    private Integer pomoCount;
}
