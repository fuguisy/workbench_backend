package com.xiaogui.workbench.module.checkin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.checkin.entity.Checkin;
import com.xiaogui.workbench.module.checkin.mapper.CheckinMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckinService extends ServiceImpl<CheckinMapper, Checkin> {

    public List<Checkin> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Checkin>().eq(Checkin::getUserId, userId).orderByDesc(Checkin::getCreateTime));
    }

    public PageResult<Checkin> getPage(Long userId, int current, int size) {
        Page<Checkin> page = new Page<>(current, size);
        Page<Checkin> result = this.page(page, new LambdaQueryWrapper<Checkin>().eq(Checkin::getUserId, userId).orderByDesc(Checkin::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Checkin entity) {
        this.save(entity);
    }

    public void update(Checkin entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
