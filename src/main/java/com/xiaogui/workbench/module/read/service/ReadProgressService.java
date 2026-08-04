package com.xiaogui.workbench.module.read.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.read.entity.ReadProgress;
import com.xiaogui.workbench.module.read.mapper.ReadProgressMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadProgressService extends ServiceImpl<ReadProgressMapper, ReadProgress> {

    public List<ReadProgress> list(Long userId) {
        return this.list(new LambdaQueryWrapper<ReadProgress>().eq(ReadProgress::getUserId, userId).orderByDesc(ReadProgress::getCreateTime));
    }

    public PageResult<ReadProgress> getPage(Long userId, int current, int size) {
        Page<ReadProgress> page = new Page<>(current, size);
        Page<ReadProgress> result = this.page(page, new LambdaQueryWrapper<ReadProgress>().eq(ReadProgress::getUserId, userId).orderByDesc(ReadProgress::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(ReadProgress entity) {
        this.save(entity);
    }

    public void update(ReadProgress entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
