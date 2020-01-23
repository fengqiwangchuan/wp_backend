package com.njucm;

import com.njucm.client.GoodsClient;
import com.njucm.common.pojo.PageResult;
import com.njucm.item.pojo.SpuBo;
import com.njucm.pojo.Goods;
import com.njucm.repository.GoodsRepository;
import com.njucm.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WpSearchService.class)
public class EsTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    SearchService searchService;

    @Autowired
    GoodsRepository goodsRepository;
    @Test
    public void create() {
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
    }

    @Test
    public void delete() {
        elasticsearchTemplate.deleteIndex(Goods.class);
    }

    @Test
    public void loadData() {
        int page = 2;
        int rows = 100;
        int size = 0;
//        do {
            PageResult<SpuBo> result = goodsClient.querySpuByPage(page, true, rows, null);
            List<SpuBo> spus = result.getItems();
            size = spus.size();
            System.out.println("size: " + size);
            List<Goods> goodsList = new ArrayList<>();
            for (SpuBo spu : spus) {
                try {
                    Goods goods = searchService.buildGoods(spu);
                    goodsList.add(goods);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            System.out.println(goodsList.size());

            goodsRepository.saveAll(goodsList);
//            page++;
//        } while (size == 100);
    }
}

