package com.xiaogui.workbench.module.calendar.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.calendar.entity.CalendarEvent;
import com.xiaogui.workbench.module.calendar.mapper.CalendarEventMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalendarEventService extends ServiceImpl<CalendarEventMapper, CalendarEvent> {

    public List<CalendarEvent> list(Long userId) {
        return this.list(new LambdaQueryWrapper<CalendarEvent>().eq(CalendarEvent::getUserId, userId).orderByAsc(CalendarEvent::getStartTime));
    }

    public PageResult<CalendarEvent> getPage(Long userId, int current, int size) {
        Page<CalendarEvent> page = new Page<>(current, size);
        Page<CalendarEvent> result = this.page(page, new LambdaQueryWrapper<CalendarEvent>().eq(CalendarEvent::getUserId, userId).orderByDesc(CalendarEvent::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(CalendarEvent entity) {
        this.save(entity);
    }

    public void update(CalendarEvent entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        CalendarEvent exists = this.getOne(new LambdaQueryWrapper<CalendarEvent>().eq(CalendarEvent::getId, entity.getId()).eq(CalendarEvent::getUserId, entity.getUserId()));
        if (exists == null) {
            throw new BizException("更新失败：数据不存在或无权限");
        }
        this.updateById(entity);
    }

    public void delete(Long id, Long userId) {
        CalendarEvent exists = this.getOne(new LambdaQueryWrapper<CalendarEvent>().eq(CalendarEvent::getId, id).eq(CalendarEvent::getUserId, userId));
        if (exists == null) {
            throw new BizException("删除失败：数据不存在或无权限");
        }
        this.removeById(id);
    }
}
