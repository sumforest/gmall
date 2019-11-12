package com.sen.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sen.gmal.api.beans.PmsSkuInfo;
import com.sen.gmal.api.service.PmsSkuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: Sen
 * @Date: 2019/11/3 17:54
 * @Description:
 */
@Controller
@CrossOrigin
@ResponseBody
public class SkuController {

    @Reference
    PmsSkuService skuService;

    @RequestMapping("/saveSkuInfo")
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo) {
        skuService.saveSkuInfo(pmsSkuInfo);
        return "success";
    }


}
