package com.xiaogui.workbench.module.markdown.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.module.markdown.entity.DocCategory;
import com.xiaogui.workbench.module.markdown.mapper.DocCategoryMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocCategoryService extends ServiceImpl<DocCategoryMapper, DocCategory> {

    /** 默认根分类名称（首次访问时自动播种） */
    private static final String[] DEFAULT_ROOT_NAMES = {
            "Skill 技能", "小说", "教程", "技术文档", "读书笔记",
            "日记随笔", "Word", "Excel", "PDF", "TXT", "其他"
    };

    /**
     * 树节点 DTO（前端 a-tree 直接可用）：
     * key 用 id 字符串，title 用 name，children 递归
     */
    public Map<String, Object> toTreeNode(DocCategory c) {
        Map<String, Object> node = new HashMap<>();
        node.put("key", "cat-" + c.getId());
        node.put("title", c.getName());
        node.put("id", c.getId());
        node.put("parentId", c.getParentId());
        node.put("sort", c.getSort());
        node.put("children", new ArrayList<Map<String, Object>>());
        return node;
    }

    /**
     * 查询用户全部分类，并组装为树结构返回。
     * 若用户无任何分类记录，则自动播种默认分类。
     */
    public List<Map<String, Object>> tree(Long userId) {
        ensureDefaults(userId);
        List<DocCategory> all = this.list(new LambdaQueryWrapper<DocCategory>()
                .eq(DocCategory::getUserId, userId)
                .orderByAsc(DocCategory::getSort)
                .orderByAsc(DocCategory::getId));
        // 组装树：parentId=0 视为根
        Map<Long, Map<String, Object>> id2Node = new HashMap<>();
        List<Map<String, Object>> roots = new ArrayList<>();
        for (DocCategory c : all) {
            id2Node.put(c.getId(), toTreeNode(c));
        }
        for (DocCategory c : all) {
            Map<String, Object> node = id2Node.get(c.getId());
            if (c.getParentId() == null || c.getParentId() == 0L) {
                roots.add(node);
            } else {
                Map<String, Object> parent = id2Node.get(c.getParentId());
                if (parent != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
                    children.add(node);
                } else {
                    // 父节点不存在，提升为根
                    roots.add(node);
                }
            }
        }
        return roots;
    }

    /** 平铺列表（前端 select 下拉用） */
    public List<DocCategory> listAll(Long userId) {
        ensureDefaults(userId);
        return this.list(new LambdaQueryWrapper<DocCategory>()
                .eq(DocCategory::getUserId, userId)
                .orderByAsc(DocCategory::getSort)
                .orderByAsc(DocCategory::getId));
    }

    public DocCategory add(Long userId, Long parentId, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BizException("分类名称不能为空");
        }
        DocCategory c = new DocCategory();
        c.setUserId(userId);
        c.setParentId(parentId == null ? 0L : parentId);
        c.setName(name.trim());
        c.setSort(0);
        this.save(c);
        return c;
    }

    public void rename(Long userId, Long id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BizException("分类名称不能为空");
        }
        DocCategory c = this.getById(id);
        if (c == null || !userId.equals(c.getUserId())) {
            throw new BizException("分类不存在或无权限");
        }
        c.setName(name.trim());
        this.updateById(c);
    }

    /**
     * 删除分类：递归删除所有子孙分类。
     * 关联的 markdown_doc.category 字段保留（字符串名），不会因删除分类而丢失文档。
     */
    public void deleteRecursive(Long userId, Long id) {
        DocCategory c = this.getById(id);
        if (c == null || !userId.equals(c.getUserId())) {
            throw new BizException("分类不存在或无权限");
        }
        List<Long> toDelete = new ArrayList<>();
        toDelete.add(id);
        collectChildren(id, toDelete);
        this.removeByIds(toDelete);
    }

    private void collectChildren(Long parentId, List<Long> out) {
        List<DocCategory> children = this.list(new LambdaQueryWrapper<DocCategory>()
                .eq(DocCategory::getParentId, parentId));
        for (DocCategory child : children) {
            out.add(child.getId());
            collectChildren(child.getId(), out);
        }
    }

    /**
     * 首次访问时为该用户播种默认根分类。
     */
    private void ensureDefaults(Long userId) {
        long count = this.count(new LambdaQueryWrapper<DocCategory>()
                .eq(DocCategory::getUserId, userId));
        if (count == 0) {
            int sort = 0;
            for (String name : DEFAULT_ROOT_NAMES) {
                DocCategory c = new DocCategory();
                c.setUserId(userId);
                c.setParentId(0L);
                c.setName(name);
                c.setSort(sort++);
                this.save(c);
            }
        }
    }
}
