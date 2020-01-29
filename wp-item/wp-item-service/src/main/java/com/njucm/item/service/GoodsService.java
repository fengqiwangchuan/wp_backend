package com.njucm.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.njucm.common.pojo.PageResult;
import com.njucm.item.mapper.*;
import com.njucm.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.html.HTMLInputElement;
import tk.mybatis.mapper.common.base.select.SelectByPrimaryKeyMapper;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    public PageResult<SpuBo> querySpuByPageAndSort(Integer page, Integer rows, Boolean saleable, String key) {
        PageHelper.startPage(page, Math.min(rows, 100));
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (saleable != null) {
            criteria.orEqualTo("saleable", saleable);
        }
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        Page<Spu> pageInfo = (Page<Spu>) spuMapper.selectByExample(example);
        List<SpuBo> list = pageInfo.getResult().stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            List<String> names = categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "/"));
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            return spuBo;
        }).collect(Collectors.toList());
        return new PageResult<>(pageInfo.getTotal(), list);
    }

    @Transactional
    public void save(SpuBo spu) {

    }

    public SpuDetail querySpuDetailById(Long id) {
        return spuDetailMapper.selectByPrimaryKey(id);
    }

    public List<Sku> querySkuBySpuId(Long spuId) {

        // 查询sku
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(record);
        for (Sku sku : skus) {
            // 同时查询出库存
            sku.setStock(stockMapper.selectByPrimaryKey(sku.getId()).getStock());
        }
        return skus;
    }

    public Spu querySpuById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }
}
