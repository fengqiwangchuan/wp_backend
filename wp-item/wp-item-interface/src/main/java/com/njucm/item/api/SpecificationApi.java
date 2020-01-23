package com.njucm.item.api;

import com.njucm.item.pojo.Specification;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("spec")
public interface SpecificationApi {

    @RequestMapping("/groups/{cid}")
    Specification querySpecificationByCategoryId(@PathVariable("cid") Long cid);
}
