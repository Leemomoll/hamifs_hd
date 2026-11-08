package com.hamifs.common;

import lombok.Data;

/**
 * 统一响应信封：{ code, message, data }
 * 与前端约定：成功 code=200；鉴权失败 401 / 403；服务端异常 500。
 */
@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.message = ResultCode.SUCCESS.getMessage();
        r.data = data;
        return r;
    }

    public static <T> Result<T> ok(T data, String message) {
        Result<T> r = ok(data);
        r.message = message;
        return r;
    }

    public static <T> Result<T> fail(ResultCode rc) {
        return fail(rc.getCode(), rc.getMessage());
    }

    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        r.data = null;
        return r;
    }

    public static <T> Result<T> fail(Integer code, String message, T data) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        r.data = data;
        return r;
    }
}