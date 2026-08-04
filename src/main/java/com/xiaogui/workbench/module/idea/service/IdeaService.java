package com.xiaogui.workbench.module.idea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.idea.entity.Idea;
import com.xiaogui.workbench.module.idea.mapper.IdeaMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdeaService extends ServiceImpl<IdeaMapper, Idea> {

    public List<Idea> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Idea>().eq(Idea::getUserId, userId).orderByDesc(Idea::getCreateTime));
    }

    public PageResult<Idea> getPage(Long userId, int current, int size) {
        Page<Idea> page = new Page<>(current, size);
        Page<Idea> result = this.page(page, new LambdaQueryWrapper<Idea>().eq(Idea::getUserId, userId).orderByDesc(Idea::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Idea entity) {
        this.save(entity);
    }

    public void update(Idea entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
