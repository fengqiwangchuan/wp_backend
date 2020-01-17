package com.njucm.item.mapper;

import com.njucm.item.pojo.Stock;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface StockMapper extends Mapper<Stock> {
}
