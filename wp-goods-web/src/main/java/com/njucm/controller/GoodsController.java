package com.njucm.controller;

import com.njucm.service.GoodsHtmlService;
import com.njucm.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("item")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @GetMapping("{id}.html")
    public String toItemPage(Model model, @PathVariable("id") Long id) {
        log.info("item : {}.html", id);
        Map<String, Object> modelMap = goodsService.loadModel(id);
        model.addAllAttributes(modelMap);

        File file = new File("/Users/weilin/Documents/微服务学习/wp_backend/wp-goods-web/target/classes/templates/" + id + ".html");

        if (file.exists()) {
            log.info("页面已静态化！spuId：{}", id);
            return id.toString();
        }
        //页面静态化
        goodsHtmlService.asyncExecute(id);

//        return "redirect:http://127.0.0.1:9003/item/" + id + ".html";
        return "item";
    }
}
