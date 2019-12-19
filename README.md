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
分类表(id,name)
品牌表(id,name)
分类与品牌关系表(category_id,brand_id)
商品表(id,title,price,category_id,brand_id)    
```
