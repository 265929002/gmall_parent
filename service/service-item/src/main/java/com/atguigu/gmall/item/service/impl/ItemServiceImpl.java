package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.client.ProductFeignClient;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ItemServiceImpl implements ItemService {
//    远程调用service-product-client

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public Map<String, Object> getBySkuId(Long skuId) {
//  Supplier T get();
        //  使用异步编排！
        //  声明对象
        Map<String, Object> result = new HashMap<>();

        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //  获取到的数据是skuInfo + skuImageList
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            result.put("skuInfo",skuInfo);
            return skuInfo;
        });
        CompletableFuture<Void> categoryViewCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            //  获取分类数据
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            result.put("categoryView", categoryView);
        });
        CompletableFuture<Void> spuSaleAttrCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            //  获取销售属性+销售属性值
            List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
            result.put("spuSaleAttrList", spuSaleAttrListCheckBySku);
        });
        CompletableFuture<Void> mapCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            //  查询销售属性值Id 与skuId 组合的map
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            //  将这个map 转换为页面需要的Json 对象
            String valueJson = JSON.toJSONString(skuValueIdsMap);
            result.put("valuesSkuJson", valueJson);

        });
        CompletableFuture<Void> priceCompletableFuture = CompletableFuture.runAsync(() -> {

            //  获取价格
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            //  map 中 key 对应的谁? Thymeleaf 获取数据的时候 ${skuInfo.skuName}

            result.put("price", skuPrice);
            //  返回map 集合 Thymeleaf 渲染：能用map 存储数据！
        });
            CompletableFuture.allOf(
                    skuInfoCompletableFuture,
                    categoryViewCompletableFuture,
                    spuSaleAttrCompletableFuture,
                    mapCompletableFuture,
                    priceCompletableFuture
            ).join();

        return result;

    }
}
