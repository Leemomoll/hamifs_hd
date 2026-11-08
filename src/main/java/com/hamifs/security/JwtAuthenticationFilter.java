package com.hamifs.security;

import com.hamifs.config.JwtProperties;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 每次请求解析 Authorization 头，把合法 JWT 写入 SecurityContext。
 *
 * 关键约定：本过滤器只负责"认领身份"，不负责"拒绝请求"。
 * - token 合法 → 写入 SecurityContext
 * - token 缺失 / 无效 / 过期 / 伪造 → 一律忽略，不设置认证，也不直接写 401，
 *   继续往下走，最终由 SecurityConfig 的 authorizeHttpRequests 决定是否放行。
 *   这样 permitAll 的接口（如 /auth/login）即使带着脏 token 也能正常响应。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProps;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse resp,
                                    FilterChain chain) throws ServletException, IOException {
        String headerValue = req.getHeader(jwtProps.getHeader());
        String token = jwtUtil.resolveFromHeader(headerValue);

        if (token != null) {
            try {
                Claims claims = jwtUtil.parse(token);
                Long userId = Long.valueOf(claims.getSubject());
                String username = claims.get("username", String.class);

                // 阶段 1：过滤器只放 subject / username；登录后的 authorities 由前端 roles 字段主导。
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                new AuthPrincipal(userId, username), null, java.util.Collections.emptyList());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // token 无效 / 过期 / 伪造：这里刻意【不拦截】，只是不设置认证，
                // 交给 SecurityConfig 的 authorizeHttpRequests 去决策：
                //   - permitAll 的接口（如 /auth/login）正常放行 —— 登录请求即使带脏 token 也能进 Controller
                //   - 需要认证的接口 → 由 authenticationEntryPoint 统一返回 401
                log.debug("JWT 解析失败，忽略该 token：{}", e.getMessage());
            }
        }
        chain.doFilter(req, resp);
    }

    /** 存进 principal 的最小信息，方便 controller 取 userId */
    public record AuthPrincipal(Long userId, String username) {}
}