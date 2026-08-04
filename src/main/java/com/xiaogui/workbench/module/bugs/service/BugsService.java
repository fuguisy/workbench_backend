package com.xiaogui.workbench.module.bugs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.bugs.entity.Bugs;
import com.xiaogui.workbench.module.bugs.mapper.BugsMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BugsService extends ServiceImpl<BugsMapper, Bugs> {

    public List<Bugs> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Bugs>().eq(Bugs::getUserId, userId).orderByDesc(Bugs::getCreateTime));
    }

    public PageResult<Bugs> getPage(Long userId, int current, int size) {
        Page<Bugs> page = new Page<>(current, size);
        Page<Bugs> result = this.page(page, new LambdaQueryWrapper<Bugs>().eq(Bugs::getUserId, userId).orderByDesc(Bugs::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Bugs entity) {
        this.save(entity);
    }

    public void update(Bugs entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
