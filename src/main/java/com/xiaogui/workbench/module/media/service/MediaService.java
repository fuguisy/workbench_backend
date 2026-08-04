package com.xiaogui.workbench.module.media.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.media.entity.Media;
import com.xiaogui.workbench.module.media.mapper.MediaMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaService extends ServiceImpl<MediaMapper, Media> {

    public List<Media> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Media>().eq(Media::getUserId, userId).orderByDesc(Media::getCreateTime));
    }

    public PageResult<Media> getPage(Long userId, int current, int size) {
        Page<Media> page = new Page<>(current, size);
        Page<Media> result = this.page(page, new LambdaQueryWrapper<Media>().eq(Media::getUserId, userId).orderByDesc(Media::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Media entity) {
        this.save(entity);
    }

    public void update(Media entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
