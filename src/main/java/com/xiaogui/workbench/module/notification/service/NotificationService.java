package com.xiaogui.workbench.module.notification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.notification.entity.Notification;
import com.xiaogui.workbench.module.notification.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService extends ServiceImpl<NotificationMapper, Notification> {

    public List<Notification> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Notification>().eq(Notification::getUserId, userId).orderByDesc(Notification::getCreateTime));
    }

    public PageResult<Notification> getPage(Long userId, int current, int size) {
        Page<Notification> page = new Page<>(current, size);
        Page<Notification> result = this.page(page, new LambdaQueryWrapper<Notification>().eq(Notification::getUserId, userId).orderByDesc(Notification::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Notification entity) {
        this.save(entity);
    }

    public void update(Notification entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        Notification exists = this.getOne(new LambdaQueryWrapper<Notification>().eq(Notification::getId, entity.getId()).eq(Notification::getUserId, entity.getUserId()));
        if (exists == null) {
            throw new BizException("更新失败：数据不存在或无权限");
        }
        this.updateById(entity);
    }

    public void delete(Long id, Long userId) {
        Notification exists = this.getOne(new LambdaQueryWrapper<Notification>().eq(Notification::getId, id).eq(Notification::getUserId, userId));
        if (exists == null) {
            throw new BizException("删除失败：数据不存在或无权限");
        }
        this.removeById(id);
    }

    public void markRead(Long id, Long userId) {
        Notification exists = this.getOne(new LambdaQueryWrapper<Notification>().eq(Notification::getId, id).eq(Notification::getUserId, userId));
        if (exists == null) {
            throw new BizException("操作失败：数据不存在或无权限");
        }
        this.update(new LambdaUpdateWrapper<Notification>().eq(Notification::getId, id).eq(Notification::getUserId, userId).set(Notification::getRead, true));
    }

    public void markAllRead(Long userId) {
        this.update(new LambdaUpdateWrapper<Notification>().eq(Notification::getUserId, userId).eq(Notification::getRead, false).set(Notification::getRead, true));
    }
}
