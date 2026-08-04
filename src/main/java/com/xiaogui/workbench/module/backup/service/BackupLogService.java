package com.xiaogui.workbench.module.backup.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.backup.entity.BackupLog;
import com.xiaogui.workbench.module.backup.mapper.BackupLogMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackupLogService extends ServiceImpl<BackupLogMapper, BackupLog> {

    public List<BackupLog> list(Long userId) {
        return this.list(new LambdaQueryWrapper<BackupLog>().eq(BackupLog::getUserId, userId).orderByDesc(BackupLog::getCreateTime));
    }

    public PageResult<BackupLog> getPage(Long userId, int current, int size) {
        Page<BackupLog> page = new Page<>(current, size);
        Page<BackupLog> result = this.page(page, new LambdaQueryWrapper<BackupLog>().eq(BackupLog::getUserId, userId).orderByDesc(BackupLog::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(BackupLog entity) {
        this.save(entity);
    }

    public void update(BackupLog entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
