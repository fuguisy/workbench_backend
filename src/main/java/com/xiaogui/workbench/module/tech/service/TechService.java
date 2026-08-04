package com.xiaogui.workbench.module.tech.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.tech.entity.Tech;
import com.xiaogui.workbench.module.tech.mapper.TechMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechService extends ServiceImpl<TechMapper, Tech> {

    public List<Tech> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Tech>().eq(Tech::getUserId, userId).orderByDesc(Tech::getCreateTime));
    }

    public PageResult<Tech> getPage(Long userId, int current, int size) {
        Page<Tech> page = new Page<>(current, size);
        Page<Tech> result = this.page(page, new LambdaQueryWrapper<Tech>().eq(Tech::getUserId, userId).orderByDesc(Tech::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Tech entity) {
        this.save(entity);
    }

    public void update(Tech entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
