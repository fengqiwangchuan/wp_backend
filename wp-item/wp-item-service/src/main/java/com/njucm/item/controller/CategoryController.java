package com.njucm.item.controller;

import com.njucm.item.pojo.Category;
import com.njucm.item.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("list")
    public ResponseEntity<List<Category>> queryCategoryListByParentId(@RequestParam(value = "pid", defaultValue = "0") Long pid) {
        log.info("pid: {}", pid);
        if (pid == null || pid.longValue() < 0) {
            return ResponseEntity.badRequest().build();
        }

        List<Category> categoryList = categoryService.queryCategoryListByParentId(pid);

        if (CollectionUtils.isEmpty(categoryList)) {
            return ResponseEntity.notFound().build();
        }

        log.info("category_list: {}", categoryList);
        ResponseEntity<List<Category>> entity = ResponseEntity.ok(categoryList);
        log.info("response: {}", entity);
        return entity;
    }

    @RequestMapping("bid/{id}")
    public ResponseEntity<List<Category>> queryCategoryListByBrandId(@PathVariable(value = "id") Long bid) {
        log.info("bid: {}", bid);
        categoryService.queryCategoryListByBrandId(bid);
        return null;
    }
}
