package com.hamifs.controller;

import com.hamifs.common.Result;
import com.hamifs.dto.PageQuery;
import com.hamifs.dto.PageResponse;
import com.hamifs.dto.UserListItem;
import com.hamifs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理接口：前端请求 /api/user/list，代理剥掉 /api 后到达 /user/list。
 * 阶段 1：先实现 GET /user/list 一条，前端能调到 admin。
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户列表（分页）
     * 例：GET /user/list?page=1&size=10&username=adm
     *
     * 响应：{ code, message, data: { list, total, page, size } }
     */
    @GetMapping("/list")
    public Result<PageResponse<UserListItem>> list(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String username) {

        PageQuery q = new PageQuery();
        q.setPage(page);
        q.setSize(size);
        q.setUsername(username);
        return Result.ok(userService.listUsers(q));
    }
}