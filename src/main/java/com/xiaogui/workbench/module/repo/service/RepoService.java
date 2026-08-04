package com.xiaogui.workbench.module.repo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.repo.entity.Repo;
import com.xiaogui.workbench.module.repo.mapper.RepoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepoService extends ServiceImpl<RepoMapper, Repo> {

    public List<Repo> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Repo>().eq(Repo::getUserId, userId).orderByDesc(Repo::getCreateTime));
    }

    public PageResult<Repo> getPage(Long userId, int current, int size) {
        Page<Repo> page = new Page<>(current, size);
        Page<Repo> result = this.page(page, new LambdaQueryWrapper<Repo>().eq(Repo::getUserId, userId).orderByDesc(Repo::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Repo entity) {
        this.save(entity);
    }

    public void update(Repo entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
