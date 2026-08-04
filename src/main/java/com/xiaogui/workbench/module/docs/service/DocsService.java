package com.xiaogui.workbench.module.docs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.docs.entity.Docs;
import com.xiaogui.workbench.module.docs.mapper.DocsMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocsService extends ServiceImpl<DocsMapper, Docs> {

    public List<Docs> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Docs>().eq(Docs::getUserId, userId).orderByDesc(Docs::getCreateTime));
    }

    public PageResult<Docs> getPage(Long userId, int current, int size) {
        Page<Docs> page = new Page<>(current, size);
        Page<Docs> result = this.page(page, new LambdaQueryWrapper<Docs>().eq(Docs::getUserId, userId).orderByDesc(Docs::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Docs entity) {
        this.save(entity);
    }

    public void update(Docs entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
