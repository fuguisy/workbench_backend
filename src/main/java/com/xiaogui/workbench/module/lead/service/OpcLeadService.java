package com.xiaogui.workbench.module.lead.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.lead.entity.OpcLead;
import com.xiaogui.workbench.module.lead.mapper.OpcLeadMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpcLeadService extends ServiceImpl<OpcLeadMapper, OpcLead> {

    public List<OpcLead> list(Long userId) {
        return this.list(new LambdaQueryWrapper<OpcLead>().eq(OpcLead::getUserId, userId).orderByDesc(OpcLead::getCreateTime));
    }

    public PageResult<OpcLead> getPage(Long userId, int current, int size) {
        Page<OpcLead> page = new Page<>(current, size);
        Page<OpcLead> result = this.page(page, new LambdaQueryWrapper<OpcLead>().eq(OpcLead::getUserId, userId).orderByDesc(OpcLead::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(OpcLead entity) {
        this.save(entity);
    }

    public void update(OpcLead entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
