package com.njucm.vo;

import com.njucm.common.pojo.PageResult;
import com.njucm.item.pojo.Brand;
import com.njucm.item.pojo.Category;
import com.njucm.pojo.Goods;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SearchResult extends PageResult<Goods> {
    private List<Category> categories;
    private List<Brand> brands;

    private List<Map<String, Object>> specs;

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }
}
