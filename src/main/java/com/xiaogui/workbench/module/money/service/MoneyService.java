package com.xiaogui.workbench.module.money.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.money.entity.Money;
import com.xiaogui.workbench.module.money.mapper.MoneyMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoneyService extends ServiceImpl<MoneyMapper, Money> {

    public List<Money> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Money>().eq(Money::getUserId, userId).orderByDesc(Money::getCreateTime));
    }

    public PageResult<Money> getPage(Long userId, int current, int size) {
        Page<Money> page = new Page<>(current, size);
        Page<Money> result = this.page(page, new LambdaQueryWrapper<Money>().eq(Money::getUserId, userId).orderByDesc(Money::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Money entity) {
        this.save(entity);
    }

    public void update(Money entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
