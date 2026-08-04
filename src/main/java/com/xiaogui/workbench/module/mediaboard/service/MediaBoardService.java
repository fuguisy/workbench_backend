package com.xiaogui.workbench.module.mediaboard.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.mediaboard.entity.MediaBoard;
import com.xiaogui.workbench.module.mediaboard.mapper.MediaBoardMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaBoardService extends ServiceImpl<MediaBoardMapper, MediaBoard> {

    public List<MediaBoard> list(Long userId) {
        return this.list(new LambdaQueryWrapper<MediaBoard>().eq(MediaBoard::getUserId, userId).orderByDesc(MediaBoard::getCreateTime));
    }

    public PageResult<MediaBoard> getPage(Long userId, int current, int size) {
        Page<MediaBoard> page = new Page<>(current, size);
        Page<MediaBoard> result = this.page(page, new LambdaQueryWrapper<MediaBoard>().eq(MediaBoard::getUserId, userId).orderByDesc(MediaBoard::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(MediaBoard entity) {
        this.save(entity);
    }

    public void update(MediaBoard entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
