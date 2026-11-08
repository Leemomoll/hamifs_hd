package com.hamifs.security;

import com.hamifs.entity.User;
import com.hamifs.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userMapper.selectByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("账号或密码错误");
        }
        return new LoginUser(u);
    }
}