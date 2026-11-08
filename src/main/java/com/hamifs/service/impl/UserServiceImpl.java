package com.hamifs.service.impl;

import com.hamifs.dto.PageQuery;
import com.hamifs.dto.PageResponse;
import com.hamifs.dto.UserListItem;
import com.hamifs.entity.User;
import com.hamifs.mapper.UserMapper;
import com.hamifs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public PageResponse<UserListItem> listUsers(PageQuery query) {
        String username = query.getUsername();
        long total = userMapper.count(username);
        List<User> rows = total == 0
                ? List.of()
                : userMapper.list(username, query.offset(), query.limit());

        List<UserListItem> items = rows.stream().map(u ->
                UserListItem.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .name(u.getName())
                        .avatar(u.getAvatar() == null ? "" : u.getAvatar())
                        .status(u.getStatus())
                        .createdAt(u.getCreatedAt())
                        .updatedAt(u.getUpdatedAt())
                        // password 故意不映射
                        .build()
        ).toList();

        return new PageResponse<>(items, total, query.getPage(), query.getSize());
    }
}