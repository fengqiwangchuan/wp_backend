package com.njucm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.njucm.bo.SearchRequest;
import com.njucm.client.BrandClient;
import com.njucm.client.CategoryClient;
import com.njucm.client.GoodsClient;
import com.njucm.client.SpecClient;
import com.njucm.common.pojo.PageResult;
import com.njucm.item.pojo.*;
import com.njucm.pojo.Goods;
import com.njucm.repository.GoodsRepository;
import com.njucm.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private ObjectMapper mapper = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();
        // 查询商品分类名称
        List<String> names = categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        // 查询sku
        List<Sku> skus = goodsClient.querySkuBySpuId(spu.getId());
        // 查询详情
        SpuDetail spuDetail = null;
        try {
            spuDetail = goodsClient.querySpuDetailById(spu.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 处理sku,仅封装id，价格、标题、图片、并获得价格集合
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuList.add(skuMap);
        });
        // 提取公共属性
        List<Map<String, Object>> genericSpecs = null;
        if (spuDetail != null) {
            genericSpecs = mapper.readValue(spuDetail.getSpecifications(), new TypeReference<List<Map<String, Object>>>() {
            });
            // 提取特有属性
            Map<String, Object> specialSpecs = mapper.readValue(spuDetail.getSpecTemplate(), new TypeReference<Map<String, Object>>() {
            });
        }
        // 过滤规格模板，把所有可搜索的信息保存到Map中
        Map<String, Object> specMap = new HashMap<>();
        String searchable = "searchable";
        String v = "v";
        String k = "k";
        String options = "options";
        if (genericSpecs != null) {
            genericSpecs.forEach(m -> {
                List<Map<String, Object>> params = (List<Map<String, Object>>) m.get("params");
                params.forEach(spe -> {
                    if ((boolean) spe.get(searchable)) {
                        if (spe.get(v) != null) {
                            specMap.put(spe.get(k).toString(), spe.get(v));
                        } else if (spe.get(options) != null) {
                            specMap.put(spe.get(k).toString(), spe.get(options));
                        }
                    }
                });
            });
        }
        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(spu.getTitle() + " " + StringUtils.join(names, " "));
        goods.setPrice(prices);
        goods.setSkus(mapper.writeValueAsString(skuList));
        goods.setSpecs(specMap);
        return goods;
    }

    public SearchResult search(SearchRequest request) {
        String key = request.getKey();
        if (StringUtils.isBlank(key)) {
            return null;
        }

        // 构造查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

//        QueryBuilder query = buildBasicQueryWithFilter(request);

//        queryBuilder.withQuery(query);
        // 对key进行全文检索查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("all", key).operator(Operator.AND));

        Map<String, String> filter = request.getFilter();
        if (!CollectionUtils.isEmpty(filter)) {
            log.info("filter: {}", filter);
            BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();
            for (Map.Entry<String, String> entry : filter.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
//                if (k != "cid3" && k != "brandId") {
//                    k = "specs." + k + ".keyword";
//                }
                filterQueryBuilder.must(QueryBuilders.termQuery(k, v));
            }
            queryBuilder.withFilter(filterQueryBuilder);
        }
        // sourceFilter设置返回字段 只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));

        // 分页
        // 准备分页参数
        searchWithPageAndSort(queryBuilder, request);
        // 聚合
        String categoryAggName = "category";
        // 品牌聚合
        String brandAggName = "brand";
        // 商品分类聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        // 品牌聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 结果
        AggregatedPage<Goods> pageInfo = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());
        // 解析查询结果
        long total = pageInfo.getTotalElements();
        int totalPages = pageInfo.getTotalPages();
        List<Category> categories = getCategoryAggResult(pageInfo.getAggregation(categoryAggName), request);
        // 品牌聚合结果
        List<Brand> brands = getBrandAggResult(pageInfo.getAggregation(brandAggName));
        // 处理规格参数
        List<Map<String, Object>> specs = null;
        if (categories.size() == 1) {
            specs = getSpec(categories.get(0).getId(), queryBuilder);
//        specs = getSpec(76l, queryBuilder);
        }
        return new SearchResult(pageInfo.getTotalElements(), pageInfo.getTotalPages(), pageInfo.getContent(), categories, brands, specs);
    }

    private QueryBuilder buildBasicQueryWithFilter(SearchRequest request) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();
        Map<String, String> filter = request.getFilter();
        if (!CollectionUtils.isEmpty(filter)) {
            filter.forEach((k, v) -> {
                if (k != "cid3" && k != "brandId") {
                    k = "specs." + k + ".keyword";
                }
                filterQueryBuilder.must(QueryBuilders.termQuery(k, v));
            });
            queryBuilder.filter(filterQueryBuilder);
        }
        return queryBuilder;
    }

    private List<Map<String, Object>> getSpec(Long id, NativeSearchQueryBuilder queryBuilder) {
        String specsJson = specClient.querySpecificationByCategoryId(id).getSpecifications();
        // 反序列化规格
        List<Map<String, Object>> specs = null;
        List<Map<String, Object>> res = new ArrayList<>();
        try {
            specs = mapper.readValue(specsJson, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 过滤可搜索
        Set<String> strSpec = new HashSet<>();
        // 保存数值规格参数及单位
        Map<String, String> numUnits = new HashMap<>();
        String searchable = "searchable";
        String numerical = "numerical";
        String k = "k";
        String unit = "unit";
        for (Map<String, Object> spec : specs) {
            List<Map<String, Object>> params = (List<Map<String, Object>>) spec.get("params");
            params.forEach(param -> {
                if ((boolean) param.get(searchable)) {
                    if (param.containsKey(numerical) && (boolean) param.get(numerical)) {
                        numUnits.put(param.get(k).toString(), param.get(unit).toString());
                    } else {
                        strSpec.add(param.get(k).toString());
                    }
                }
            });
        }

        strSpec.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param).field("specs." + param + ".keyword"));
        });

        Map<String, Aggregation> aggs = elasticsearchTemplate.query(queryBuilder.build(), SearchResponse::getAggregations).asMap();

        for (String param : strSpec) {
            Map<String, Object> spec = new HashMap<>();
            spec.put("k", param);
            StringTerms terms = (StringTerms) aggs.get(param);
            spec.put("options", terms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString));
            res.add(spec);
        }

        // 聚合计算数值类型的interval
//        Map<String, Double> numInterval = getNumberInterval(id, numUnits.keySet());

        return res;
    }

    private Map<String, Double> getNumberInterval(Long id, Set<String> keySet) {
        Map<String, Object> numSpecs = new HashMap<>();
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.termQuery("cid3", id.toString())).withSourceFilter(new FetchSourceFilter(new String[]{""}, null)).withPageable(PageRequest.of(0, 1));
        // 添加stats类型的聚合,同时返回avg,max,min,sum,count等
        for (String key : keySet) {
//            queryBuilder.addAggregation(AggregationBuilders.stats(key).field("specs." + key));
            queryBuilder.addAggregation(AggregationBuilders.stats(key).field("specs." + key));
        }
        return null;
    }

    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        try {
            LongTerms brandAgg = (LongTerms) aggregation;
            List<Long> bids = new ArrayList<>();
            for (LongTerms.Bucket bucket : brandAgg.getBuckets()) {
                bids.add(bucket.getKeyAsNumber().longValue());
            }
            log.info("bids: {}", bids);
            return brandClient.queryBrandByIds(bids);
        } catch (Exception e) {
            log.error("品牌聚合不存在：{}", e);
            return null;
        }
    }

    private List<Category> getCategoryAggResult(Aggregation aggregation, SearchRequest request) {
        try {
            LongTerms categoryAgg = (LongTerms) aggregation;
            List<Long> cids = new ArrayList<>();
            Map<String, String> filter = request.getFilter();
            if (filter != null && filter.containsKey("cid3")) {
                cids.add(Long.valueOf(filter.get("cid3")));
            } else {
                for (LongTerms.Bucket bucket : categoryAgg.getBuckets()) {
                    cids.add(bucket.getKeyAsNumber().longValue());
                }
            }
            log.info("cids: {}", cids);
            return categoryClient.queryCategoryByIds(cids);
        } catch (Exception e) {
            log.error("分类聚合出现异常：{}", e);
            return null;
        }
    }

    /**
     * 构建基本查询条件
     *
     * @param queryBuilder
     * @param request
     */
    private void searchWithPageAndSort(NativeSearchQueryBuilder queryBuilder, SearchRequest request) {
        int page = request.getPage();
        int size = request.getDefaultSize();

        // 分页
        queryBuilder.withPageable(PageRequest.of(page - 1, size));
        // 排序
        String sortBy = request.getSortBy();
        Boolean desc = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)) {
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
    }
}
