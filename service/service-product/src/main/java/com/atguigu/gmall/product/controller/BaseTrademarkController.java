package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//品牌管理
@RestController
@RequestMapping("admin/product/baseTrademark")
public class BaseTrademarkController {
    @Autowired
    private BaseTrademarkService baseTrademarkService;
//    http://api.gmall.com/admin/product/baseTrademark/{page}/{limit}

    /**
     * 品牌信息无条件分页展示信息
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("{page}/{limit}")
    public Result getBaseTradeMarkList(@PathVariable Long page,
                                       @PathVariable Long limit){
        Page<BaseTrademark> baseTrademarkPage = new Page<>(page, limit);
        IPage iPage = baseTrademarkService.getBaseTradeMarkList(baseTrademarkPage);
        return Result.ok(iPage);
    }

    //http://api.gmall.com/admin/product/baseTrademark/save     添加品牌
//    {tmName: "锤子。", logoUrl: "/static/default.jpg"}

    /**
     * 添加品牌
     * @param baseTrademark 将json转换为java对象      品牌属性表
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody BaseTrademark baseTrademark){
        return Result.ok(baseTrademarkService.save(baseTrademark));
    }
    //http://api.gmall.com/admin/product/baseTrademark/update
    //修改品牌  put请求     baseTrademark的json字符串

    /**
     *修改品牌
     * @return
     */
    @PutMapping("update")
    public Result update(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    //http://api.gmall.com/admin/product/baseTrademark/remove/{id}
    //delete请求    删除品牌

    /**
     * 根据id删品牌
     * @return
     */
    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

    /**
     * 5、根据Id获取品牌
     * http://api.gmall.com/admin/product/baseTrademark/get/{id}
     */
    @GetMapping("get/{id}")
    public Result getBaseTradeMarkList(@PathVariable Long id){
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);

        return Result.ok(baseTrademark);
    }
    //  http://api.gmall.com/admin/product/baseTrademark/getTrademarkList
    @GetMapping("getTrademarkList")
    public Result getTrademarkList(){


        return Result.ok(baseTrademarkService.list(null));
    }

}
