package com.njucm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.njucm.client.BrandClient;
import com.njucm.client.CategoryClient;
import com.njucm.client.GoodsClient;
import com.njucm.client.SpecClient;
import com.njucm.item.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class GoodsService {

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private SpecClient specClient;

    public Map<String, Object> loadModel(Long spuId) {
        Spu spu = goodsClient.querySpuById(spuId);
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spuId);
        List<Sku> skus = goodsClient.querySkuBySpuId(spuId);
        Brand brand = brandClient.queryBrandByIds(Arrays.asList(spu.getBrandId())).get(0);

        List<Category> categories = getCategories(spu);
        String specsJson = spuDetail.getSpecifications();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> allSpecs = null;
        try {
            allSpecs = objectMapper.readValue(specsJson, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Map<Integer, String> specName = new HashMap<>();
        Map<Integer, Object> specValue = new HashMap<>();
        getAllSpec(allSpecs, specName, specValue);

        //获取特有规格参数
        String specTemplate = spuDetail.getSpecTemplate();
        Map<String, String[]> specs = null;
        try {
            specs = objectMapper.readValue(specTemplate, new TypeReference<Map<String, String[]>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Map<Integer, String> specialParamName = new HashMap<>();
        Map<Integer, String[]> specialParamValue = new HashMap<>();
        getSpecialSpec(specs, specName, specValue, specialParamName, specialParamValue);

        //按组构造规格参数
        List<Map<String, Object>> groups = getGroupsSpec(allSpecs, specName, specValue);
        Map<String, Object> map = new HashMap<>();
        map.put("spu", spu);
        map.put("spuDetail", spuDetail);
        map.put("skus", skus);
        map.put("brand", brand);
        map.put("categories", categories);
        map.put("specName", specName);
        map.put("specValue", specValue);
        map.put("groups", groups);
        map.put("specialParamName", specialParamName);
        map.put("specialParamValue", specialParamValue);

        return map;
    }

    private List<Map<String, Object>> getGroupsSpec(List<Map<String, Object>> specs, Map<Integer, String> specName, Map<Integer, Object> specValue) {
        List<Map<String, Object>> groups = new ArrayList<>();
        int i = 0;
        int j = 0;
        for (Map<String, Object> spec : specs) {
            List<Map<String, Object>> params = (List<Map<String, Object>>) spec.get("params");
            List<Map<String, Object>> tmp = new ArrayList<>();
            for (Map<String, Object> param : params) {
                for (Map.Entry<Integer, String> entry : specName.entrySet()) {
                    if (entry.getValue().equals(param.get("k").toString())) {
                        String value = specValue.get(entry.getKey()) != null ? specValue.get(entry.getKey()).toString() : "无";
                        Map<String, Object> tmp3 = new HashMap<>(16);
                        tmp3.put("id", ++j);
                        tmp3.put("name", entry.getValue());
                        tmp3.put("value", value);
                        tmp.add(tmp3);
                    }

                }
            }
            Map<String, Object> tmp2 = new HashMap<>(16);
            tmp2.put("params", tmp);
            tmp2.put("id", ++i);
            tmp2.put("name", spec.get("group"));
            groups.add(tmp2);
        }
        return groups;
    }

    private void getSpecialSpec(Map<String, String[]> specs, Map<Integer, String> specName, Map<Integer, Object> specValue, Map<Integer, String> specialParamName, Map<Integer, String[]> specialParamValue) {
        if (specs != null) {
            for (Map.Entry<String, String[]> entry : specs.entrySet()) {
                String key = entry.getKey();
                for (Map.Entry<Integer, String> e : specName.entrySet()) {
                    if (e.getValue().equals(key)) {
                        specialParamName.put(e.getKey(), e.getValue());
                        String s = specValue.get(e.getKey()).toString();
                        String result = StringUtils.substring(s, 1, s.length() - 1);
                        specialParamValue.put(e.getKey(), result.split(","));
                    }
                }
            }
        }
    }

    private List<Category> getCategories(Spu spu) {
        Category c1 = null;
        Category c2 = null;
        Category c3 = null;
        try {
            List<String> names = categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            c1 = new Category();
            c1.setId(spu.getCid1());
            c1.setName(names.get(0));
            c2 = new Category();
            c2.setId(spu.getCid2());
            c2.setName(names.get(1));
            c3 = new Category();
            c3.setId(spu.getCid3());
            c3.setName(names.get(2));
            return Arrays.asList(c1, c2, c3);
        } catch (Exception e) {
            log.error("查询商品分类出错，spuId：{}", spu.getId(), e);
        }
        return null;
    }

    private void getAllSpec
            (List<Map<String, Object>> specs, Map<Integer, String> specName, Map<Integer, Object> specValue) {
        String k = "k";
        String v = "v";
        String unit = "unit";
        String numerical = "numerical";
        String options = "options";
        int i = 0;
        if (specs != null) {
            for (Map<String, Object> s : specs) {
                List<Map<String, Object>> params = (List<Map<String, Object>>) s.get("params");
                for (Map<String, Object> param : params) {
                    String result;
                    if (param.get(v) == null) {
                        result = "无";
                    } else {
                        result = param.get(v).toString();
                    }
                    if (param.containsKey(numerical) && (boolean) param.get(numerical)) {
                        if (result.contains(".")) {
                            Double d = Double.valueOf(result);
                            if (d.intValue() == d) {
                                result = d.intValue() + "";
                            }
                        }
                        i++;
                        specName.put(i, param.get(k).toString());
                        specValue.put(i, result + param.get(unit));
                    } else if (param.containsKey(options)) {
                        i++;
                        specName.put(i, param.get(k).toString());
                        specValue.put(i, param.get(options));
                    } else {
                        i++;
                        specName.put(i, param.get(k).toString());
                        specValue.put(i, param.get(v));
                    }
                }
            }
        }
    }
}
