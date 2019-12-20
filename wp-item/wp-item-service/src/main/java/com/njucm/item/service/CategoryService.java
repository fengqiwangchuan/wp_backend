package com.njucm.item.service;

import com.njucm.item.mapper.CategoryMapper;
import com.njucm.item.pojo.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<Category> queryCategoryListByBrandId(Long bid) {
        List<Long> cids = categoryMapper.queryCategoryIdByBrandId(bid);
        List<Category> categoryList = new ArrayList<>();
        for (Long cid : cids) {
            Category category = categoryMapper.selectByPrimaryKey(cid);
            categoryList.add(category);
        }
        return categoryList;
    }

}
