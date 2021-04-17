package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ManageService {

/*
        1.先加载所有一级分类id
        2.通过一级分类id加载2级分类数据
        3.通过二级分类id加载三级分类数据
        4.根据分类id加载平台属性列表

 */

    /**
     * 获取所有1级分类数据，初始化加载
     * @return
     */
        List<BaseCategory1> getBaseCategory1();

    /**
     * 通过1级分类属性的id来查询2级分类数据
     * @param category1Id 1级分类属性id
     * @return
     */
        List<BaseCategory2> getBaseCategory2ById(Long category1Id);

    /**
     * 通过2级分类属性的id来查询3级分类数据
     * @param category2Id 2级分类属性id
     * @return
     */
    List<BaseCategory3> getBaseCategory3ById(Long category2Id);


//     4.根据分类id加载平台属性列表
//    BaseAttrValue  平台属性值
//      BaseAttrInfo  平台属性
    List<BaseAttrInfo> getBaseAttrInfoList(Long category1Id,Long category2Id,Long category3Id);

    /**
     * 保存平台属性
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(Long attrId);

    /**
     * 根据平台属性值id获取平台属性值集合
     * @param attrId
     * @return
     */
    BaseAttrInfo getbaseAttrInfo(Long attrId);

    /**
     * 根据三级分类id分页查询商品表，
     * @param spuInfoPage 分页条件 page和limit
     * @param spuInfo   三级分类id
     * @return
     */
    IPage<SpuInfo> getSpuInfoList(Page<SpuInfo> spuInfoPage, SpuInfo spuInfo);

    /**
     * 获取所有的销售属性列表
     * @return
     */
    List<BaseSaleAttr> getbaseSaleAttrList();

    /**
     * 保存spuInfo数据  商品属性数据
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 根据spuId获取图片列表
     * @param spuId
     * @return
     */
    List<SpuImage> getSpuImageList(Long spuId);

    /**
     * 根据spuId获取销售属性
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> spuSaleAttrList(Long spuId);

//    sku添加
    void saveSkuInfo(SkuInfo skuInfo);

    IPage getSkuInfoLsit(Page<SkuInfo> skuInfoPage);

    void onSale(Long skuId);

    void cancelSale(Long skuId);


    /**
     * 根据skuId 查询skuInfo 和skuImage
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(Long skuId);


    /**
     * 通过三级分类id查询分类信息
     * @param category3Id
     * @return
     */
    BaseCategoryView getCategoryViewByCategory3Id(Long category3Id);

    /**
     * 根据skuId查询商品价格
     * @param skuId
     * @return
     */
    BigDecimal getSkuPrice(Long skuId);

    /**
     * 根据spuId，skuId 查询销售属性集合
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    /**
     * 根据spuid获取销售属性值id与skuid的组合数据
     * @param spuId
     * @return
     */
    Map getSkuValueIdsMap(Long spuId);
}
