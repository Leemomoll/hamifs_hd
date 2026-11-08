package com.hamifs.controller;

import com.hamifs.common.Result;
import com.hamifs.dto.LoginRequest;
import com.hamifs.dto.LoginResponse;
import com.hamifs.dto.UserInfoResponse;
import com.hamifs.service.AuthService;
import com.hamifs.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 鉴权接口。
 * 路径说明：前端请求统一带 /api 前缀（浏览器看到 /api/auth/login），
 * 但 Vite dev 代理与生产 Nginx 都会把 /api 剥掉后再转发，
 * 因此后端这里注册的是不带 /api 的路径。
 *  - POST /auth/login
 *  - GET  /auth/info
 *  - POST /auth/logout
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.ok(authService.login(req));
    }

    @GetMapping("/info")
    public Result<UserInfoResponse> info() {
        Long userId = authServiceImpl.currentUserId();
        return Result.ok(authService.info(userId));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }
}