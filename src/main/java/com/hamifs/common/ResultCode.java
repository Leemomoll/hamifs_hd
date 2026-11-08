package com.hamifs.common;

import lombok.Getter;

/**
 * 业务响应码枚举。
 * 注意：HTTP 状态码 与 此处的业务 code 必须同时返回。
 *  - 401：Token 过期 / 无效 / 未登录
 *  - 403：无权限
 *  - 500：服务端异常
 *  - 200：业务成功
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "ok"),

    // 业务参数
    BAD_REQUEST(400, "请求参数错误"),

    // 鉴权
    UNAUTHORIZED(401, "登录已过期，请重新登录"),
    TOKEN_INVALID(401, "Token 无效"),
    FORBIDDEN(403, "无权访问"),

    // 账号相关
    USER_NOT_FOUND(404, "账号或密码错误"),
    BAD_CREDENTIALS(401, "账号或密码错误"),
    ACCOUNT_DISABLED(403, "账号已被禁用"),

    // 服务端
    ERROR(500, "服务器异常");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}