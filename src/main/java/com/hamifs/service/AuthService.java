package com.hamifs.service;

import com.hamifs.dto.LoginRequest;
import com.hamifs.dto.LoginResponse;
import com.hamifs.dto.UserInfoResponse;

public interface AuthService {

    LoginResponse login(LoginRequest req);

    UserInfoResponse info(Long userId);

    /** 阶段 1：无黑名单，logout 只是占位。 */
    void logout();
}