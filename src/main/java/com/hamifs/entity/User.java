package com.hamifs.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体（sys_user）。
 * 注意：password 字段严禁通过接口对外返回。
 */
@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String avatar;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}