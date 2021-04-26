package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)//捕获异常会回滚
public class ManageServiceImpl implements ManageService {

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;
    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;
    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;
    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;
    @Resource
    private SpuInfoMapper spuInfoMapper;
    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Resource
    private SpuImageMapper spuImageMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;
    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;

    @Override
    public List<BaseCategory1> getBaseCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory2> getBaseCategory2ById(Long category1Id) {
        return baseCategory2Mapper.selectList(new QueryWrapper<BaseCategory2>().eq("category1_id",category1Id));
    }

    @Override
    public List<BaseCategory3> getBaseCategory3ById(Long category2Id) {
        return baseCategory3Mapper.selectList(new QueryWrapper<BaseCategory3>().eq("category2_id",category2Id));
    }

    @Override
    public List<BaseAttrInfo> getBaseAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoList(category1Id,category2Id,category3Id);
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
//判断id是否为空，空就是添加非空就是修改
        if (baseAttrInfo.getId()!=null){
            baseAttrInfoMapper.updateById(baseAttrInfo);
        }else {
            baseAttrInfoMapper.insert(baseAttrInfo);
        }

        //先删除在新增 base_attr_value
        baseAttrValueMapper.delete(new QueryWrapper<BaseAttrValue>().eq("attr_id",baseAttrInfo.getId()));


//   先获取平台属性值集合
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
//        判断集合不为空
        if (!CollectionUtils.isEmpty(attrValueList)){
//            循环遍历当前集合，插入到平台属性表中
            for(BaseAttrValue baseAttrValue : attrValueList){
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {

        return baseAttrValueMapper.selectList(new QueryWrapper<BaseAttrValue>()
                .eq("attr_id",attrId));
    }

    @Override
    public BaseAttrInfo getbaseAttrInfo(Long attrId) {
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(attrId);
        if (baseAttrInfo!=null){
            //  获取平台属性值集合数据
            List<BaseAttrValue> attrValueList = getAttrValueList(attrId);
            if (!CollectionUtils.isEmpty(attrValueList)){
                baseAttrInfo.setAttrValueList(attrValueList);
            }
        }
        //  返回数据
        return baseAttrInfo;
    }
    /**
     * 根据三级分类id分页查询商品表，
     * @param spuInfoPage 分页条件 page和limit
     * @param spuInfo   三级分类id
     * @return
     */
    @Override
    public IPage<SpuInfo> getSpuInfoList(Page<SpuInfo> spuInfoPage, SpuInfo spuInfo) {
        QueryWrapper<SpuInfo> spuInfoQueryWrapper = new QueryWrapper<>();
        spuInfoQueryWrapper.eq("category3_id",spuInfo.getCategory3Id());
//        设置一个排序
        spuInfoQueryWrapper.orderByDesc("id");//倒叙
        return spuInfoMapper.selectPage(spuInfoPage,spuInfoQueryWrapper);
    }

    @Override
    public List<BaseSaleAttr> getbaseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrs = baseSaleAttrMapper.selectList(null);
        return baseSaleAttrs;
    }

    /**
     * 商品属性spu保存
     * @param spuInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuInfo(SpuInfo spuInfo) {
        //添加spuInfo
        spuInfoMapper.insert(spuInfo);
        //  先获取到spuImageList 集合数据
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (!CollectionUtils.isEmpty(spuImageList)){

            for (SpuImage spuImage : spuImageList) {
                //  将spuId 进行赋值
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            }

        }
        //  获取当前的销售属性集合
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (!CollectionUtils.isEmpty(spuSaleAttrList)){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                //  将spuId 进行赋值
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);
                //  获取当前的销售属性值集合

                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (!CollectionUtils.isEmpty(spuSaleAttrValueList)){
                    //  循环遍历
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        //  将spuId 进行赋值
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        //  赋值销售属性名称
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }
        }


    }

    /**
     * 根据spuId获取图片列表
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {

        return spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id",spuId));
    }

    /**
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
//        List<SpuSaleAttr>  spuSaleAttrList = spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
        return spuSaleAttrs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuInfo(SkuInfo skuInfo) {
//      sku_info
//      sku_attr_value
//        sku_sale_attr_value
//        sku_image


        //      sku_info
        skuInfoMapper.insert(skuInfo);
//      sku_attr_value
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
//  获取 sku_sale_attr_value
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (!CollectionUtils.isEmpty(skuSaleAttrValueList)){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
        //        sku_image

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (!CollectionUtils.isEmpty(skuImageList)){
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            }
        }

    }

    @Override
    public IPage getSkuInfoLsit(Page<SkuInfo> skuInfoPage) {
        QueryWrapper<SkuInfo> skuInfoQueryWrapper = new QueryWrapper<>();
        skuInfoQueryWrapper.orderByDesc("id");
        return skuInfoMapper.selectPage(skuInfoPage, skuInfoQueryWrapper);

    }

    @Override
    public void onSale(Long skuId) {
        //  更新状态
        //  update  sku_info set is_sale = 1 where id = 45;
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public void cancelSale(Long skuId) {
        //  更新状态
        //  update  sku_info set is_sale = 0 where id = 45;
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
    }

    /**
     * 根据skuId查询skuInfo和skuImage
     * @param skuId
     * @return
     */
    @Override
    @GmallCache(prefix = "sku:")
    public SkuInfo getSkuInfo(Long skuId) {
//        查询skuInfo信息
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
//        创建查询条件
        QueryWrapper<SkuImage> skuImageQueryWrapper = new QueryWrapper<>();
        skuImageQueryWrapper.eq("sku_id",skuId);
//        查询spuImage信息
        List<SkuImage> skuImages = skuImageMapper.selectList(skuImageQueryWrapper);
        skuInfo.setSkuImageList(skuImages);
        return skuInfo;
    }

    @Override
    @GmallCache(prefix = "Category3:")
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id) {

        return baseCategoryViewMapper.selectById(category3Id);
    }

    /**
     * 根据skuId查询商品价格
     * @param skuId
     * @return
     */
    @Override
    @GmallCache(prefix = "Price:")
    public BigDecimal getSkuPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuInfo!=null){
//            不为空则返回商品价格
            return skuInfo.getPrice();
        }
        //为空就返回默认值0
        return new BigDecimal(0);
    }

    /**
     * 根据spuId，skuId 查询销售属性集合
     * @param skuId
     * @param spuId
     * @return
     */
    @Override
    @GmallCache(prefix = "SaleAttrListCheck:")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuId, spuId);
    }

    /**
     *
     * @param spuId
     * @return
     */
    @Override
    @GmallCache(prefix = "SkuValueIdsMap")
    public Map getSkuValueIdsMap(Long spuId) {
        Map map = new HashMap();

        List<Map> mapList = skuSaleAttrValueMapper.selectSaleAttrValuesBySpu(spuId);
        if (!StringUtils.isEmpty(mapList)){
            for (Map maps : mapList) {
                map.put(maps.get("value_ids"),maps.get("sku_id"));
            }

        }

        return map;
    }

    /**
     * 获取全部分类信息
     * @return
     */
    @Override
    @GmallCache(prefix = "BaseCategoryList:")
    public List<JSONObject> getBaseCategoryList() {
        // 声明几个json 集合
        ArrayList<JSONObject> list = new ArrayList<>();
        //获取所有分类数据集合
        List<BaseCategoryView> baseCategoryViewList = baseCategoryViewMapper.selectList(null);
        //  按照一级分类Id 进行分组
        //  key = category1Id   value = List<BaseCategoryView>
        Map<Long, List<BaseCategoryView>> collect
                = baseCategoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //  声明一个index
        int index = 1;
        //  循环遍历当前的集合数据                             //entrySet可使用它对map进行遍历。
        Iterator<Map.Entry<Long, List<BaseCategoryView>>> iterator = collect.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Long, List<BaseCategoryView>> next = iterator.next();
            //  获取对应的key ，value
            Long category1Id = next.getKey();
            List<BaseCategoryView> category2List = next.getValue();

            //  获取一级分类的名称       类似从集合获取数据，获取第一条
            String categoryName = category2List.get(0).getCategory1Name();

            JSONObject category1 = new JSONObject();
            category1.put("index",index);
            category1.put("categoryName",categoryName);
            category1.put("categoryId",category1Id);
            //索引迭代
            index++;
//---------------------------------------------------------------------------------------------------------
            //获取二级分类数据：
            Map<Long, List<BaseCategoryView>> category2Map =
                    category2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            //  声明一个二级分类集合数据
            List<JSONObject> category2Child = new ArrayList<>();
            Iterator<Map.Entry<Long, List<BaseCategoryView>>> iterator1 = category2Map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Long, List<BaseCategoryView>> next1 = iterator1.next();
                Long category2Id = next1.getKey();
                List<BaseCategoryView> category3List = next1.getValue();
                //  获取二级分类名称
                String category2Name = category3List.get(0).getCategory2Name();
                //  赋值
                JSONObject category2 = new JSONObject();
                category2.put("categoryName",category2Name);
                category2.put("categoryId",category2Id);

                //  声明一个集合来存储所有的二级分类数据！
                category2Child.add(category2);
//--------------------------------------------------------------------------------------------
                //  声明一个三级分类集合数据
                List<JSONObject> category3Child = new ArrayList<>();
                //  获取三级分类数据,三级数据 要全都遍历，所以直接for循环
                category3List.forEach((baseCategoryView)->{
                    JSONObject category3 = new JSONObject();
                    category3.put("categoryName",baseCategoryView.getCategory3Name());
                    category3.put("categoryId",baseCategoryView.getCategory3Id());
                    category3Child.add(category3);
                });
                //将3级放到2机中
                category2.put("categoryChild",category3Child);
            }
            //将2级数据放到1级分类中
            category1.put("categoryChild",category2Child);
//            将1级分类数据放到list集合中
            list.add(category1);
        }

        //  循环遍历当前的集合数据
        return list;
    }

    /**
     * 根据skuid获取品牌数据
     * @param tmId
     * @return
     */
    @Override
    public BaseTrademark getTrademarkByTmId(Long tmId) {

        return baseTrademarkMapper.selectById(tmId);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(Long skuId) {

        return baseAttrInfoMapper.selectBaseAttrInfoListBySkuId(skuId);
    }


}
