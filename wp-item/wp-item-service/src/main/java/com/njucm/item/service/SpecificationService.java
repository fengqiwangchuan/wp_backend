package com.njucm.item.service;

import com.njucm.item.mapper.SpecificationMapper;
import com.njucm.item.pojo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;

    public Specification queryById(Long cid) {
        return specificationMapper.selectByPrimaryKey(cid);
    }
}
