package com.njucm.item.service;

import com.njucm.item.mapper.CategoryMapper;
import com.njucm.item.pojo.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryListByParentId(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        log.info("record: {}", record);
        List<Category> list = categoryMapper.select(record);

        return list;

    }
}
