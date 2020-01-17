package com.njucm.item.controller;

import com.njucm.common.pojo.PageResult;
import com.njucm.item.pojo.Sku;
import com.njucm.item.pojo.SpuBo;
import com.njucm.item.pojo.SpuDetail;
import com.njucm.item.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                            @RequestParam(value = "saleable", required = false) Boolean saleable,
                                                            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                                            @RequestParam(value = "key", required = false) String key) {
        PageResult<SpuBo> result = goodsService.querySpuByPageAndSort(page, rows, saleable, key);
        if (result == null || result.getItems().size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spu) {
        log.info("spu: {}", spu);
        goodsService.save(spu);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id") Long id) {
        SpuDetail detail = goodsService.querySpuDetailById(id);
        if (detail == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(detail);
    }

    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id) {
        List<Sku> skus = goodsService.querySkuBySpuId(id);
        if (skus == null || skus.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(skus);
    }
}
