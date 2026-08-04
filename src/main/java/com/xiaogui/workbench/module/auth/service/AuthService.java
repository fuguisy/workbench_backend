package com.xiaogui.workbench.module.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.module.auth.dto.LoginDTO;
import com.xiaogui.workbench.module.auth.dto.LoginVO;
import com.xiaogui.workbench.module.auth.dto.RegisterDTO;
import com.xiaogui.workbench.module.auth.entity.SysUser;
import com.xiaogui.workbench.module.auth.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Resource
    private SysUserMapper sysUserMapper;

    public LoginVO login(LoginDTO dto) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        StpUtil.login(user.getId());
        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        return vo;
    }

    public void register(RegisterDTO dto) {
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new BizException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        user.setNickname(dto.getNickname() == null ? dto.getUsername() : dto.getNickname());
        user.setRole("user");
        sysUserMapper.insert(user);
    }

    public SysUser getCurrentUser() {
        long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return user;
    }

    public void logout() {
        StpUtil.logout();
    }
}
