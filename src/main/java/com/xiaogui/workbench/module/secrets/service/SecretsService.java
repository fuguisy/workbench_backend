package com.xiaogui.workbench.module.secrets.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.secrets.entity.Secrets;
import com.xiaogui.workbench.module.secrets.mapper.SecretsMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecretsService extends ServiceImpl<SecretsMapper, Secrets> {

    public List<Secrets> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Secrets>().eq(Secrets::getUserId, userId).orderByDesc(Secrets::getCreateTime));
    }

    public PageResult<Secrets> getPage(Long userId, int current, int size) {
        Page<Secrets> page = new Page<>(current, size);
        Page<Secrets> result = this.page(page, new LambdaQueryWrapper<Secrets>().eq(Secrets::getUserId, userId).orderByDesc(Secrets::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Secrets entity) {
        this.save(entity);
    }

    public void update(Secrets entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
