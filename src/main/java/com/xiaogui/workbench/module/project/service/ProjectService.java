package com.xiaogui.workbench.module.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.project.entity.Project;
import com.xiaogui.workbench.module.project.mapper.ProjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService extends ServiceImpl<ProjectMapper, Project> {

    public List<Project> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Project>().eq(Project::getUserId, userId).orderByDesc(Project::getCreateTime));
    }

    public PageResult<Project> getPage(Long userId, int current, int size) {
        Page<Project> page = new Page<>(current, size);
        Page<Project> result = this.page(page, new LambdaQueryWrapper<Project>().eq(Project::getUserId, userId).orderByDesc(Project::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Project entity) {
        this.save(entity);
    }

    public void update(Project entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
