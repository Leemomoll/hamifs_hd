package com.hamifs.security;

import com.hamifs.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类：签发 / 解析 / 校验。
 * 阶段 1：无 Redis 黑名单，Token 真失效依赖过期时间。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties props;

    private SecretKey key;

    @PostConstruct
    void init() {
        byte[] bytes = props.getSecret().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("jwt.secret 长度不足 32 字节");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * 签发 Token。
     * @param userId   用户主键
     * @param username 登录名
     * @param ttlSeconds 有效期（秒）—— 登录接口按 rememberMe 决定
     * @return JWT 字符串
     */
    public String issue(Long userId, String username, long ttlSeconds) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .issuer(props.getIssuer())
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(new Date(now))
                .expiration(new Date(now + ttlSeconds * 1000L))
                .signWith(key)
                .compact();
    }

    /** 解析并校验 Token。失败抛 JwtException。 */
    public Claims parse(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(props.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** 从请求头中提取 token。返回 null 表示不存在或格式不对。 */
    public String resolveFromHeader(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return null;
        }
        String prefix = props.getTokenPrefix();
        if (!headerValue.startsWith(prefix)) {
            return null;
        }
        String t = headerValue.substring(prefix.length()).trim();
        return t.isEmpty() ? null : t;
    }

    public long getDefaultExpireSecond() {
        return props.getExpireSeconds();
    }

    public long getRememberMeExpireSecond() {
        return props.getRememberMeSeconds();
    }
}