package com.njucm.bo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    // 搜索条件
    private String key;
    // 当前页
    private Integer page;
    // 排序字段
    private String sortBy;
    // 是否降序
    private Boolean descending;
    // 过滤字段
    private Map<String, String> filter;
    // 每页大小 不从页面接收，而是固定大小
    private static final Integer DEFAULT_SIZE = 20;
    // 默认页
    private static final Integer DEFAULT_PAGE = 1;

    public Integer getPage() {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        // 不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }

    public Integer getDefaultSize() {
        return DEFAULT_SIZE;
    }
}
