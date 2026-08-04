package com.xiaogui.workbench.config;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaogui.workbench.module.auth.entity.SysUser;
import com.xiaogui.workbench.module.auth.mapper.SysUserMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataInitializer {

    @Resource
    private SysUserMapper sysUserMapper;

    @PostConstruct
    public void init() {
        long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin")
        );
        if (count == 0) {
            SysUser admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
            admin.setNickname("晓贵");
            admin.setRole("admin");
            sysUserMapper.insert(admin);
            log.info("===== 默认管理员已创建：admin / admin123 =====");
        } else {
            log.info("===== 管理员账号已存在，跳过初始化 =====");
        }
    }
}
