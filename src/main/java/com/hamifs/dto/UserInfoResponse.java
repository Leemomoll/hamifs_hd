package com.hamifs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户信息响应 data 结构：
 *  { id, username, name, avatar, roles, permissions, menus }
 * 阶段 1：roles/permissions 硬编码，menus 为空，阶段 2 再读库。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private Long id;
    private String username;
    private String name;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
    private List<?> menus;
}