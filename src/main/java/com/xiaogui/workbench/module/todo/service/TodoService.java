package com.xiaogui.workbench.module.todo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.todo.entity.Todo;
import com.xiaogui.workbench.module.todo.mapper.TodoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService extends ServiceImpl<TodoMapper, Todo> {

    public List<Todo> list(Long userId) {
        return this.list(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId));
    }

    public PageResult<Todo> getPage(Long userId, int current, int size) {
        Page<Todo> page = new Page<>(current, size);
        Page<Todo> result = this.page(page,
                new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId).orderByDesc(Todo::getCreateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(Todo todo) {
        this.save(todo);
    }

    public void update(Todo todo) {
        if (todo.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        this.updateById(todo);
    }

    public void delete(Long id) {
        this.removeById(id);
    }

    public void toggleDone(Long id) {
        Todo todo = this.getById(id);
        if (todo == null) {
            throw new BizException("任务不存在");
        }
        Todo update = new Todo();
        update.setId(id);
        update.setDone(todo.getDone() != null && todo.getDone() == 1 ? 0 : 1);
        this.updateById(update);
    }
}
