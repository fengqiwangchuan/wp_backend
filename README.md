# wp_backend
leyou 商城 开源项目学习 

## wp-register
    Eureka 注册中心
    
## wp-api-gateway
    zuul 网关

## wp-item
    商品微服务
    
## wp-common
    通用工具模块

## 商品与分类与品牌
* 分类与品牌 多对多
* 品牌与商品 一对多
* 分类层级关系
```
简化
分类表(id,name)
品牌表(id,name)
分类与品牌关系表(category_id,brand_id)
商品表(id,title,price,category_id,brand_id)    
```

## 商品规格管理

> 商品的规格参数是分类绑定的

* spu
* sku

```
一个分类对应一个规格参数模版
一个分类多个商品
分类表() 1:n 商品表(spu) 1:n sku表
分类表 1:1 规格参数模版
规格参数模版  spu通用规格  sku特有规格
```

## 搜索

> 使用规格参数中的部分 作为搜索条件

