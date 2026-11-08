package com.hamifs.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理：把异常统一翻译为 {code,message,data} 信封，
 * 并设置 HTTP 状态码（401/403/500）。
 *
 * Spring Security 的认证/鉴权异常由 SecurityConfig 中的
 * AuthenticationEntryPoint / AccessDeniedHandler 优先处理（带 token 时）。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** @Valid 校验失败 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(ResultCode.BAD_REQUEST.getCode(), msg));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<?>> handleBind(BindException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(ResultCode.BAD_REQUEST.getCode(), msg));
    }

    /** 业务抛出的已知错误 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<?>> handleIllegal(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(ResultCode.BAD_REQUEST.getCode(), ex.getMessage()));
    }

    /** 账号密码错误（Authentication 子类） */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Result<?>> handleBadCreds(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.fail(ResultCode.BAD_CREDENTIALS.getCode(),
                        ResultCode.BAD_CREDENTIALS.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<?>> handleAuth(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.fail(ResultCode.UNAUTHORIZED.getCode(),
                        ResultCode.UNAUTHORIZED.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<?>> handleDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.fail(ResultCode.FORBIDDEN.getCode(),
                        ResultCode.FORBIDDEN.getMessage()));
    }

    /** 兜底：所有未捕获异常 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleAll(Exception ex) {
        log.error("未捕获异常", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.fail(ResultCode.ERROR.getCode(),
                        ResultCode.ERROR.getMessage()));
    }
}