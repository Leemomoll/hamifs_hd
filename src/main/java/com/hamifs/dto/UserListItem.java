package com.hamifs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户列表项 VO（对外）。绝不返回 password 字段。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListItem {

    private Long id;
    private String username;
    private String name;
    private String avatar;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}