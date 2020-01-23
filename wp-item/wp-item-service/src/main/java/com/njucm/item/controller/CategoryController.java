package com.njucm.item.controller;

import com.njucm.item.pojo.Category;
import com.njucm.item.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
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
        List<Category> categoryList = categoryService.queryCategoryListByBrandId(bid);
        return ResponseEntity.ok(categoryList);
    }

    @GetMapping("names")
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("ids") List<Long> ids) {
        List<String> list = categoryService.queryNameByIds(ids);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("all")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> ids) {
        List<Category> list = categoryService.queryCategoryByIds(ids);
        if (list == null || list.size() < 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @GetMapping("all/level/{id}")
    public ResponseEntity<List<Category>> queryAllByCid3(@PathVariable("id") Long id) {
        List<Category> list = categoryService.queryNameByCid3(id);
        if (list == null || list.size() < 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(list);
        }
    }
}
