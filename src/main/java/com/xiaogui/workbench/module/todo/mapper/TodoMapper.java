package com.xiaogui.workbench.module.todo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaogui.workbench.module.todo.entity.Todo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TodoMapper extends BaseMapper<Todo> {
}
