package com.xiaogui.workbench.module.todo.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.common.Result;
import com.xiaogui.workbench.module.todo.entity.Todo;
import com.xiaogui.workbench.module.todo.service.TodoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/todo")
public class TodoController {

    @Resource
    private TodoService todoService;

    @GetMapping("/list")
    public Result<List<Todo>> list() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.ok(todoService.list(userId));
    }

    @GetMapping("/page")
    public Result<PageResult<Todo>> page(@RequestParam(defaultValue = "1") int current,
                                         @RequestParam(defaultValue = "10") int size) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.ok(todoService.getPage(userId, current, size));
    }

    @PostMapping("")
    public Result<Void> add(@RequestBody Todo todo) {
        Long userId = StpUtil.getLoginIdAsLong();
        todo.setUserId(userId);
        todoService.add(todo);
        return Result.ok();
    }

    @PutMapping("")
    public Result<Void> update(@RequestBody Todo todo) {
        todoService.update(todo);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        todoService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/toggle")
    public Result<Void> toggle(@PathVariable Long id) {
        todoService.toggleDone(id);
        return Result.ok();
    }
}
