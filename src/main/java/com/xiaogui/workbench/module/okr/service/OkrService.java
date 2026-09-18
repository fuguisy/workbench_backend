package com.xiaogui.workbench.module.okr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.okr.entity.OkrKeyResult;
import com.xiaogui.workbench.module.okr.entity.OkrObjective;
import com.xiaogui.workbench.module.okr.mapper.OkrObjectiveMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OkrService extends ServiceImpl<OkrObjectiveMapper, OkrObjective> {

    private final OkrKeyResultService krService;

    public OkrService(OkrKeyResultService krService) {
        this.krService = krService;
    }

    public List<OkrObjective> list(Long userId) {
        return this.list(new LambdaQueryWrapper<OkrObjective>().eq(OkrObjective::getUserId, userId).orderByDesc(OkrObjective::getCreateTime));
    }

    public PageResult<OkrObjective> getPage(Long userId, int current, int size) {
        Page<OkrObjective> page = new Page<>(current, size);
        Page<OkrObjective> result = this.page(page, new LambdaQueryWrapper<OkrObjective>().eq(OkrObjective::getUserId, userId).orderByDesc(OkrObjective::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(OkrObjective entity) {
        this.save(entity);
    }

    public void update(OkrObjective entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        OkrObjective exists = this.getOne(new LambdaQueryWrapper<OkrObjective>().eq(OkrObjective::getId, entity.getId()).eq(OkrObjective::getUserId, entity.getUserId()));
        if (exists == null) {
            throw new BizException("更新失败：数据不存在或无权限");
        }
        this.updateById(entity);
    }

    public void delete(Long id, Long userId) {
        OkrObjective exists = this.getOne(new LambdaQueryWrapper<OkrObjective>().eq(OkrObjective::getId, id).eq(OkrObjective::getUserId, userId));
        if (exists == null) {
            throw new BizException("删除失败：数据不存在或无权限");
        }
        // 级联删除该O下的全部KR
        krService.deleteByObjective(id, userId);
        this.removeById(id);
    }
}
