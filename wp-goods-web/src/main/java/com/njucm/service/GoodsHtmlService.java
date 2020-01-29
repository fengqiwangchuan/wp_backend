package com.njucm.service;

import com.njucm.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
@Service
public class GoodsHtmlService {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private TemplateEngine templateEngine;

    public void createHtml(Long spuId) {
        PrintWriter writer = null;
        Map<String, Object> spuMap = goodsService.loadModel(spuId);
        Context context = new Context();
        context.setVariables(spuMap);
        File file_1 = new File("/Users/weilin/Documents/微服务学习/wp_backend/wp-goods-web/target/classes/templates/" + spuId + ".html");
        File file_2 = new File("/Users/weilin/Documents/微服务学习/wp_backend/wp-goods-web/src/main/resources/templates/" + spuId + ".html");
        try {
            writer = new PrintWriter(file_1);
            templateEngine.process("item", context, writer);
            writer = new PrintWriter(file_2);
            templateEngine.process("item", context, writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void deleteHtml(Long id) {
        File file_1 = new File("/Users/weilin/Documents/微服务学习/wp_backend/wp-goods-web/target/classes/templates/" + id + ".html");
        File file_2 = new File("/Users/weilin/Documents/微服务学习/wp_backend/wp-goods-web/src/main/resources/templates/" + id + ".html");
        file_1.deleteOnExit();
        file_2.deleteOnExit();
    }

    public void asyncExecute(Long spuId) {
        ThreadUtils.execute(() -> createHtml(spuId));
    }
}
