package com.xiaogui.workbench.module.markdown.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaogui.workbench.common.Result;
import com.xiaogui.workbench.module.markdown.entity.DocCategory;
import com.xiaogui.workbench.module.markdown.service.DocCategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doc-category")
public class DocCategoryController {

    @Resource
    private DocCategoryService service;

    /** 获取分类树（含首次播种默认分类） */
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> tree() {
        return Result.ok(service.tree(StpUtil.getLoginIdAsLong()));
    }

    /** 平铺列表（下拉选择用） */
    @GetMapping("/list")
    public Result<List<DocCategory>> list() {
        return Result.ok(service.listAll(StpUtil.getLoginIdAsLong()));
    }

    /** 新增分类（根分类传 parentId=0） */
    @PostMapping
    public Result<DocCategory> add(@RequestBody Map<String, Object> body) {
        Long parentId = body.get("parentId") == null ? 0L
                : Long.parseLong(String.valueOf(body.get("parentId")));
        String name = body.get("name") == null ? "" : String.valueOf(body.get("name"));
        return Result.ok(service.add(StpUtil.getLoginIdAsLong(), parentId, name));
    }

    /** 重命名分类 */
    @PutMapping("/{id}")
    public Result<Void> rename(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String name = body.get("name") == null ? "" : String.valueOf(body.get("name"));
        service.rename(StpUtil.getLoginIdAsLong(), id, name);
        return Result.ok();
    }

    /** 删除分类（递归删除子分类） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.deleteRecursive(StpUtil.getLoginIdAsLong(), id);
        return Result.ok();
    }
}
