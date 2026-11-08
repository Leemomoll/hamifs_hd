package com.hamifs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 绑定 application.yml 中的 jwt.* 配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** 签名密钥（原文，>= 32 字节） */
    private String secret;

    /** 默认 accessToken 有效期（秒） */
    private Long expireSeconds;

    /** 记住我有效期（秒） */
    private Long rememberMeSeconds;

    /** 请求头名称 */
    private String header = "Authorization";

    /** Token 前缀（含末尾空格） */
    private String tokenPrefix = "Bearer ";

    /** 签发者 */
    private String issuer = "hamifs-back";
}