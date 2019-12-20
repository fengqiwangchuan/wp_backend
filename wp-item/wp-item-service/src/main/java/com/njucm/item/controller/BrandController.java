package com.njucm.item.controller;

import com.njucm.common.pojo.PageParam;
import com.njucm.common.pojo.PageResult;
import com.njucm.item.pojo.Brand;
import com.njucm.item.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                              @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                                              @RequestParam(value = "sortBy", required = false) String sortBy,
                                                              @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
                                                              @RequestParam(value = "key", required = false) String key) {
        PageParam pageParam = PageParam.builder().page(page).rows(rows).sortBy(sortBy).desc(desc).key(key).build();
        log.info("pageParam: {}", pageParam);
        PageResult<Brand> result = brandService.queryBrandByPageAndSort(pageParam);
        if (result == null || result.getItems().size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("create")
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        log.info("brand: {}-cids: {}", brand, cids);
        brandService.saveBrand(brand, cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("update")
    public ResponseEntity<Void> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        log.info("brand: {}-cids: {}", brand, cids);
        brandService.updateBrand(brand, cids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
