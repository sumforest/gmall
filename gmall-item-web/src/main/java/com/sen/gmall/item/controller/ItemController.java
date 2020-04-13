package com.sen.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.sen.gmal.api.beans.PmsProductSaleAttr;
import com.sen.gmal.api.beans.PmsSkuInfo;
import com.sen.gmal.api.service.PmsSkuService;
import com.sen.gmal.api.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.Map;

/**
 * @Auther: Sen
 * @Date: 2019/11/3 23:31
 * @Description: 商品详情页
 */
@Controller
public class ItemController {

    @Reference
    PmsSkuService skuService;

    @Reference
    SpuService spuService;

    @GetMapping("/index")
    public String showIndex(ModelMap modelMap) {

        modelMap.put("hello", "hello thymeleaf");

        return "index";
    }

    @GetMapping("/{skuId}.html")
    public String showItem(@PathVariable String skuId, ModelMap modelMap) {
        //封装sku对象属性
        PmsSkuInfo pmsSkuInfo = skuService.getSkuInfoById(skuId);
        modelMap.put("skuInfo", pmsSkuInfo);

        //封装sku销售属性
        List<PmsProductSaleAttr> productSaleAttrs = spuService.spuSaleAttrListCheckBySku(skuId, pmsSkuInfo.getSpuId());
        modelMap.put("spuSaleAttrListCheckBySku", productSaleAttrs);

        //封装spu下的所有的sku信息和saleAttr信息
        Map<String,String> map = skuService.getSkuInfoAndSaleAttrValues(pmsSkuInfo.getSpuId());
        String skuInfoJsonStr = JSON.toJSONString(map);
        modelMap.put("skuInfoJsonStr", skuInfoJsonStr);
        return "item";
    }

}
