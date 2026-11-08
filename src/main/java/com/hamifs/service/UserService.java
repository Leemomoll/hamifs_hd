package com.hamifs.service;

import com.hamifs.dto.PageQuery;
import com.hamifs.dto.PageResponse;
import com.hamifs.dto.UserListItem;

public interface UserService {

    /** 用户列表（分页）。永远不返回 password 字段。 */
    PageResponse<UserListItem> listUsers(PageQuery query);
}