package com.hamifs.service.impl;

import com.hamifs.common.ResultCode;
import com.hamifs.dto.LoginRequest;
import com.hamifs.dto.LoginResponse;
import com.hamifs.dto.UserInfoResponse;
import com.hamifs.entity.User;
import com.hamifs.mapper.UserMapper;
import com.hamifs.security.JwtAuthenticationFilter.AuthPrincipal;
import com.hamifs.security.JwtUtil;
import com.hamifs.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authManager;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest req) {
        // 1. 走 Spring Security 的账号密码校验（自动调 UserDetailsService + PasswordEncoder）
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (BadCredentialsException e) {
            // 阶段 1 统一提示，不区分账号是否存在
            throw new BadCredentialsException(ResultCode.BAD_CREDENTIALS.getMessage());
        }

        // 2. 加载完整用户信息用于签发 token
        User user = userMapper.selectByUsername(req.getUsername());
        if (user == null) {
            throw new BadCredentialsException(ResultCode.BAD_CREDENTIALS.getMessage());
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new org.springframework.security.authentication.DisabledException(
                    ResultCode.ACCOUNT_DISABLED.getMessage());
        }

        // 3. 签发 Token（按 rememberMe 决定有效期）
        boolean remember = Boolean.TRUE.equals(req.getRememberMe());
        long ttl = remember ? jwtUtil.getRememberMeExpireSecond() : jwtUtil.getDefaultExpireSecond();
        String token = jwtUtil.issue(user.getId(), user.getUsername(), ttl);

        return new LoginResponse(token, ttl);
    }

    @Override
    public UserInfoResponse info(Long userId) {
        User u = userMapper.selectById(userId);
        if (u == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 阶段 1：roles / permissions 写死，menus 先返回空
        return UserInfoResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .name(u.getName() != null ? u.getName() : u.getUsername())
                .avatar(u.getAvatar() != null ? u.getAvatar() : "")
                .roles(List.of("admin"))
                .permissions(List.of(
                        "system:user:list",
                        "system:user:add",
                        "system:user:edit",
                        "system:user:delete",
                        "system:role:list",
                        "system:menu:list"
                ))
                .menus(List.of())
                .build();
    }

    @Override
    public void logout() {
        // 阶段 1：无黑名单，logout 仅返回 200；前端兜底清本地状态。
        // 拿到当前 token 可选：写日志 / 留 hook 后续接黑名单
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr != null) {
            HttpServletRequest req = attr.getRequest();
            org.slf4j.LoggerFactory.getLogger(AuthServiceImpl.class)
                    .info("用户登出：{}", req.getHeader("Authorization"));
        }
    }

    /** controller 取当前登录用户主键的辅助方法 */
    public Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthPrincipal p)) {
            return null;
        }
        return p.userId();
    }
}