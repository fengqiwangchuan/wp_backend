package com.njucm.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.njucm.common.pojo.PageParam;
import com.njucm.common.pojo.PageResult;
import com.njucm.item.mapper.BrandMapper;
import com.njucm.item.pojo.Brand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Slf4j
@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;


    public PageResult<Brand> queryBrandByPageAndSort(PageParam pageParam) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getRows());
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(pageParam.getKey())) {
            example.createCriteria().andLike("name", "%" + pageParam.getKey() + "%").orEqualTo("letter", pageParam.getKey());
        }
        if (StringUtils.isNotBlank(pageParam.getSortBy())) {
            String orderByClause = pageParam.getSortBy() + (pageParam.getDesc() ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        Page<Brand> pageInfo = (Page<Brand>) brandMapper.selectByExample(example);
        log.info("pageInfo: {}", pageInfo);
        return new PageResult<>(pageInfo.getTotal(), pageInfo);
    }

    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        brandMapper.insertSelective(brand);
        for (Long cid : cids) {
            brandMapper.insertCategoryBrand(cid, brand.getId());
        }
    }

    @Transactional
    public void updateBrand(Brand brand, List<Long> cids) {

    }

    public List<Brand> queryBrandByCategory(Long cid) {
        return brandMapper.queryByCategoryId(cid);
    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
        return brandMapper.selectByIdList(ids);
    }
}
