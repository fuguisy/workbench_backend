package com.xiaogui.workbench.module.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.ai.entity.AiLab;
import com.xiaogui.workbench.module.ai.mapper.AiLabMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiLabService extends ServiceImpl<AiLabMapper, AiLab> {

    public List<AiLab> list(Long userId) {
        return this.list(new LambdaQueryWrapper<AiLab>().eq(AiLab::getUserId, userId).orderByDesc(AiLab::getCreateTime));
    }

    public PageResult<AiLab> getPage(Long userId, int current, int size) {
        Page<AiLab> page = new Page<>(current, size);
        Page<AiLab> result = this.page(page, new LambdaQueryWrapper<AiLab>().eq(AiLab::getUserId, userId).orderByDesc(AiLab::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(AiLab entity) {
        this.save(entity);
    }

    public void update(AiLab entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
