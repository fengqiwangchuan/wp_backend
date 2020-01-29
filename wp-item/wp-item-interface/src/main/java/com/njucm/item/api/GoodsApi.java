package com.njucm.item.api;

import com.njucm.common.pojo.PageResult;
import com.njucm.item.pojo.Sku;
import com.njucm.item.pojo.Spu;
import com.njucm.item.pojo.SpuBo;
import com.njucm.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//@RequestMapping("goods")
public interface GoodsApi {
    @GetMapping("/spu/page")
    PageResult<SpuBo> querySpuByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                     @RequestParam(value = "saleable", required = false) Boolean saleable,
                                     @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                     @RequestParam(value = "key", required = false) String key);


    @GetMapping("/spu/detail/{id}")
    SpuDetail querySpuDetailById(@PathVariable("id") Long id);

    @GetMapping("sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id") Long id);

    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id") Long id);

}
