package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.client.ProductFeignClient;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {
//    远程调用service-product-client

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public Map<String, Object> getBySkuId(Long skuId) {
//        Map<String, Object> result = new HashMap<>();
////        通过skuId 获取skuInfo 和skuImage
//        SkuInfo skuInfo = productFeignClient.getAttrValueList(skuId);
//        //      通过skuid和spuId  查销售属性集合
//        List<SpuSaleAttr> skuValueIdsMap = productFeignClient.getSpuSaleAttrListCheckBySku(skuInfo.getId(), skuInfo.getSpuId());
//
////          通过三级分类id查询分类信息
//        BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
////        获取商品价格
//        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
//        //        //根据spuId 查询map 集合属性
//        Map skuValueIdsMap1 = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
//        //分类信息
//        result.put("categoryView",categoryView);
//        //将map集合转化为JSON字符串
//        String valueSkuJSON = JSON.toJSONString(skuValueIdsMap1);
//        // 获取价格
//        result.put("price",skuPrice);
//        // 保存valuesSkuJson
//        result.put("valuesSkuJson",valueSkuJSON);
//        // 保存数据
//        result.put("spuSaleAttrList",skuValueIdsMap);
//        // 保存skuInfo
//        result.put("skuInfo",skuInfo);
//        return result;


        //  声明对象
        Map<String, Object> result = new HashMap<>();

        //  获取到的数据是skuInfo + skuImageList
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        //  判断skuInfo 不为空
        if (skuInfo != null) {
            //  获取分类数据
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            result.put("categoryView", categoryView);
            //  获取销售属性+销售属性值
            List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
            result.put("spuSaleAttrList", spuSaleAttrListCheckBySku);
            //  查询销售属性值Id 与skuId 组合的map
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            //  将这个map 转换为页面需要的Json 对象
            String valueJson = JSON.toJSONString(skuValueIdsMap);
            result.put("valuesSkuJson", valueJson);

        }
        //  获取价格
        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
        //  map 中 key 对应的谁? Thymeleaf 获取数据的时候 ${skuInfo.skuName}
        result.put("skuInfo", skuInfo);
        result.put("price", skuPrice);
        //  返回map 集合 Thymeleaf 渲染：能用map 存储数据！
        return result;

    }
}
