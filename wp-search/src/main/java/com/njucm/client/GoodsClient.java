package com.njucm.client;

import com.njucm.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {

}
