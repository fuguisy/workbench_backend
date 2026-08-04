package com.xiaogui.workbench.module.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.svc.entity.Svc;
import com.xiaogui.workbench.module.svc.mapper.SvcMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SvcService extends ServiceImpl<SvcMapper, Svc> {

    public List<Svc> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Svc>().eq(Svc::getUserId, userId).orderByDesc(Svc::getCreateTime));
    }

    public PageResult<Svc> getPage(Long userId, int current, int size) {
        Page<Svc> page = new Page<>(current, size);
        Page<Svc> result = this.page(page, new LambdaQueryWrapper<Svc>().eq(Svc::getUserId, userId).orderByDesc(Svc::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Svc entity) {
        this.save(entity);
    }

    public void update(Svc entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
