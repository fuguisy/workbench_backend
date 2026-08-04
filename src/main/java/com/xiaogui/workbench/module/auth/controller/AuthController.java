package com.xiaogui.workbench.module.auth.controller;

import com.xiaogui.workbench.common.Result;
import com.xiaogui.workbench.module.auth.dto.LoginDTO;
import com.xiaogui.workbench.module.auth.dto.LoginVO;
import com.xiaogui.workbench.module.auth.dto.RegisterDTO;
import com.xiaogui.workbench.module.auth.entity.SysUser;
import com.xiaogui.workbench.module.auth.service.AuthService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.ok(authService.login(dto));
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterDTO dto) {
        authService.register(dto);
        return Result.ok();
    }

    @GetMapping("/info")
    public Result<SysUser> info() {
        SysUser user = authService.getCurrentUser();
        user.setPassword(null);
        return Result.ok(user);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }
}
