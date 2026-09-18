package com.xiaogui.workbench.module.focus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.focus.entity.FocusRecord;
import com.xiaogui.workbench.module.focus.mapper.FocusRecordMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FocusService extends ServiceImpl<FocusRecordMapper, FocusRecord> {

    public List<FocusRecord> list(Long userId) {
        return this.list(new LambdaQueryWrapper<FocusRecord>().eq(FocusRecord::getUserId, userId).orderByDesc(FocusRecord::getCreateTime));
    }

    public PageResult<FocusRecord> getPage(Long userId, int current, int size) {
        Page<FocusRecord> page = new Page<>(current, size);
        Page<FocusRecord> result = this.page(page, new LambdaQueryWrapper<FocusRecord>().eq(FocusRecord::getUserId, userId).orderByDesc(FocusRecord::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(FocusRecord entity) {
        this.save(entity);
    }

    public void update(FocusRecord entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        FocusRecord exists = this.getOne(new LambdaQueryWrapper<FocusRecord>().eq(FocusRecord::getId, entity.getId()).eq(FocusRecord::getUserId, entity.getUserId()));
        if (exists == null) {
            throw new BizException("更新失败：数据不存在或无权限");
        }
        this.updateById(entity);
    }

    public void delete(Long id, Long userId) {
        FocusRecord exists = this.getOne(new LambdaQueryWrapper<FocusRecord>().eq(FocusRecord::getId, id).eq(FocusRecord::getUserId, userId));
        if (exists == null) {
            throw new BizException("删除失败：数据不存在或无权限");
        }
        this.removeById(id);
    }
}
