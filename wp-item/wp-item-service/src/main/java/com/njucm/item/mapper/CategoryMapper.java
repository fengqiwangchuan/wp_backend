package com.njucm.item.mapper;

import com.njucm.item.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category, Long> {

    @Select("SELECT category_id from tb_category_brand where brand_id=#{bid}")
    List<Long> queryCategoryIdByBrandId(@Param("bid") Long bid);

}
