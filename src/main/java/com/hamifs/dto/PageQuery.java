package com.hamifs.dto;

import lombok.Data;

/**
 * 分页查询入参：?page=1&size=10&username=xxx
 */
@Data
public class PageQuery {

    /** 页码（从 1 开始） */
    private Integer page = 1;

    /** 每页条数（默认 10，最大 100） */
    private Integer size = 10;

    /** 用户名模糊匹配（可选） */
    private String username;

    /** 计算偏移 */
    public int offset() {
        int p = page == null || page < 1 ? 1 : page;
        int s = size == null || size < 1 ? 10 : size;
        if (s > 100) s = 100;
        return (p - 1) * s;
    }

    public int limit() {
        int s = size == null || size < 1 ? 10 : size;
        if (s > 100) s = 100;
        return s;
    }
}