package com.hamifs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应 data 结构：{ list, total, page, size }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> list;
    private long total;
    private int page;
    private int size;
}