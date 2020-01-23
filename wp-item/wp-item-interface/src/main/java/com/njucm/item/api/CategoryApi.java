package com.njucm.item.api;

import com.njucm.item.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("category")
public interface CategoryApi {
    @GetMapping("names")
    List<String> queryNameByIds(@RequestParam("ids") List<Long> ids);

    @GetMapping("all")
    List<Category> queryCategoryByIds(@RequestParam("ids") List<Long> ids);
}
