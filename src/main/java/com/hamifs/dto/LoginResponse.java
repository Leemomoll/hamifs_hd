package com.hamifs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应：data.token / data.expiresIn
 * 与前端约定（user.js）：token 是字符串，expiresIn 是秒。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    /** 单位：秒 */
    private Long expiresIn;
}