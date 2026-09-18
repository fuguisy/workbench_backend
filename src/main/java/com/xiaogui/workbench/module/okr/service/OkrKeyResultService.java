package com.xiaogui.workbench.module.okr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.module.okr.entity.OkrKeyResult;
import com.xiaogui.workbench.module.okr.mapper.OkrKeyResultMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OkrKeyResultService extends ServiceImpl<OkrKeyResultMapper, OkrKeyResult> {

    /** 查询某O目标下的全部KR */
    public List<OkrKeyResult> listByObjective(Long objectiveId, Long userId) {
        return this.list(new LambdaQueryWrapper<OkrKeyResult>()
                .eq(OkrKeyResult::getObjectiveId, objectiveId)
                .eq(OkrKeyResult::getUserId, userId)
                .orderByAsc(OkrKeyResult::getCreateTime));
    }

    /** 查询用户全部KR（用于跨O统计） */
    public List<OkrKeyResult> listAllByUser(Long userId) {
        return this.list(new LambdaQueryWrapper<OkrKeyResult>()
                .eq(OkrKeyResult::getUserId, userId));
    }

    public void add(OkrKeyResult entity) {
        if (entity.getObjectiveId() == null) {
            throw new BizException("新增KR失败：必须关联O目标ID");
        }
        if (entity.getWeight() == null) entity.setWeight(1);
        if (entity.getProgressMode() == null) entity.setProgressMode("MANUAL");
        if (entity.getStatus() == null) entity.setStatus("ACTIVE");
        this.save(entity);
    }

    public void update(OkrKeyResult entity) {
        if (entity.getId() == null) {
            throw new BizException("更新KR失败：id不能为空");
        }
        OkrKeyResult exists = this.getOne(new LambdaQueryWrapper<OkrKeyResult>()
                .eq(OkrKeyResult::getId, entity.getId())
                .eq(OkrKeyResult::getUserId, entity.getUserId()));
        if (exists == null) {
            throw new BizException("更新KR失败：数据不存在或无权限");
        }
        this.updateById(entity);
    }

    public void delete(Long id, Long userId) {
        OkrKeyResult exists = this.getOne(new LambdaQueryWrapper<OkrKeyResult>()
                .eq(OkrKeyResult::getId, id)
                .eq(OkrKeyResult::getUserId, userId));
        if (exists == null) {
            throw new BizException("删除KR失败：数据不存在或无权限");
        }
        this.removeById(id);
    }

    /** 删除某O目标下的全部KR（O被删除时调用） */
    public void deleteByObjective(Long objectiveId, Long userId) {
        this.remove(new LambdaQueryWrapper<OkrKeyResult>()
                .eq(OkrKeyResult::getObjectiveId, objectiveId)
                .eq(OkrKeyResult::getUserId, userId));
    }
}
