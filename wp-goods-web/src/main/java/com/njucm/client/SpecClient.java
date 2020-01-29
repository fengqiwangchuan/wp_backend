package com.njucm.client;

import com.njucm.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
public interface SpecClient extends SpecificationApi {
}
