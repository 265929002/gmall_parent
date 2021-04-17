package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // @ResponseBody + @Controller
@RequestMapping("admin/product")
public class SpuManageController {
    @Autowired
   private ManageService manageService;

//    http://api.gmall.com/admin/product/{page}/{limit}?category3Id=61  get
//page：第几页
//limit：每页数量
//category3Id：三级分类ID

    //String category3Id 名称一致，自动映射
    @GetMapping("{page}/{limit}")
    public Result getSpuInfoList(@PathVariable Long page,
                                 @PathVariable Long limit,
                                 SpuInfo spuInfo){//传递的参数被实体类所包含，可以用实体类来接受
//分页查询  ,泛型是spu商品表的实体类
        Page<SpuInfo> spuInfoPage = new Page<>(page,limit);
//        调用接口方法实现分页查询,将查询的数据进行返回
        IPage<SpuInfo> iPage = manageService.getSpuInfoList(spuInfoPage,spuInfo);
        return Result.ok(iPage);
    }
    //http://api.gmall.com/admin/product/baseSaleAttrList
//    获取销售属性

    @GetMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrs = manageService.getbaseSaleAttrList();
        return Result.ok(baseSaleAttrs);
    }
   // http://api.gmall.com/admin/product/saveSpuInfo

    /**
     *保存spuInfo数据  商品属性数据
     * @return
     */
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){//用实体类对象接收
        manageService.saveSpuInfo(spuInfo);

        return Result.ok();
    }



//    http://api.gmall.com/admin/product/spuImageList/{spuId}
//    根据spuId获取图片列表
    @GetMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable Long spuId){
        List<SpuImage> spuImageList = manageService.getSpuImageList(spuId);
        return Result.ok(spuImageList);
    }

//    http://api.gmall.com/admin/product/spuSaleAttrList/{spuId}
//    根据spuId获取销售属性
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable Long spuId){
        List<SpuSaleAttr> spuSaleAttrs =  manageService.spuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrs);
    }



}
