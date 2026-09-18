package com.xiaogui.workbench.module.subscription.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.subscription.entity.Subscription;
import com.xiaogui.workbench.module.subscription.mapper.SubscriptionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService extends ServiceImpl<SubscriptionMapper, Subscription> {

    public List<Subscription> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Subscription>().eq(Subscription::getUserId, userId).orderByDesc(Subscription::getCreateTime));
    }

    public PageResult<Subscription> getPage(Long userId, int current, int size) {
        Page<Subscription> page = new Page<>(current, size);
        Page<Subscription> result = this.page(page, new LambdaQueryWrapper<Subscription>().eq(Subscription::getUserId, userId).orderByDesc(Subscription::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Subscription entity) {
        this.save(entity);
    }

    public void update(Subscription entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        Subscription exists = this.getOne(new LambdaQueryWrapper<Subscription>().eq(Subscription::getId, entity.getId()).eq(Subscription::getUserId, entity.getUserId()));
        if (exists == null) {
            throw new BizException("更新失败：数据不存在或无权限");
        }
        this.updateById(entity);
    }

    public void delete(Long id, Long userId) {
        Subscription exists = this.getOne(new LambdaQueryWrapper<Subscription>().eq(Subscription::getId, id).eq(Subscription::getUserId, userId));
        if (exists == null) {
            throw new BizException("删除失败：数据不存在或无权限");
        }
        this.removeById(id);
    }
}
