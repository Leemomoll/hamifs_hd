package com.hamifs.security;

import com.hamifs.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security 上下文中的「当前登录用户」。
 * 阶段 1：roles / permissions 写死为 ["admin"]，阶段 2 再读库。
 */
@Getter
@RequiredArgsConstructor
public class LoginUser implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 阶段 1：先硬编码一个 ROLE_ADMIN；阶段 2 从 sys_role / sys_permission 读
        return Collections.singletonList(
                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_admin")
        );
    }

    @Override public String getPassword()              { return user.getPassword(); }
    @Override public String getUsername()              { return user.getUsername(); }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return user.getStatus() != null && user.getStatus() == 1; }
}