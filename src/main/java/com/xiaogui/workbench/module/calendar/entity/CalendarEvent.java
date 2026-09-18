package com.xiaogui.workbench.module.calendar.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaogui.workbench.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("calendar_event")
public class CalendarEvent extends BaseEntity {

    private Long userId;
    private String title;
    private String category;
    private String location;
    private String attendees;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean allDay;
    private String priority;
    private String status;
}
