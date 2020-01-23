package com.njucm.item.service;

import com.njucm.item.mapper.CategoryMapper;
import com.njucm.item.pojo.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<String> queryNameByIds(List<Long> ids) {
        return categoryMapper.selectByIdList(ids).stream().map(Category::getName).collect(Collectors.toList());
    }

    public List<Category> queryCategoryByIds(List<Long> ids) {
        return categoryMapper.selectByIdList(ids);
    }

    public List<Category> queryNameByCid3(Long id) {
        Category c3 = categoryMapper.selectByPrimaryKey(id);
        Category c2 = categoryMapper.selectByPrimaryKey(c3.getParentId());
        Category c1 = categoryMapper.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1, c2, c3);
    }
}
