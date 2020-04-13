package com.sen.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sen.gmal.api.beans.PmsProductImage;
import com.sen.gmal.api.beans.PmsProductInfo;
import com.sen.gmal.api.beans.PmsProductSaleAttr;
import com.sen.gmal.api.service.SpuService;
import com.sen.gmall.manage.util.PmsFileUploadUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/2 22:01
 * @Description: 标准化产品单元管理
 */
@Controller
@CrossOrigin
@ResponseBody
public class SpuController {

    @Reference
    private SpuService service;

    @GetMapping("/spuList")
    public List<PmsProductInfo> spuList(String catalog3Id) {
        return service.spuList(catalog3Id);
    }

    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam(value = "file") MultipartFile multipartFile) {
        return PmsFileUploadUtil.upLoadImage(multipartFile);
    }

    @RequestMapping("/saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {
        service.saverSpuInfo(pmsProductInfo);
        return "success";
    }

    @GetMapping("/spuSaleAttrList")
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {
        return service.spuSaleAttrList(spuId);
    }

    @GetMapping("/spuImageList")
    public List<PmsProductImage> spuImageList(String spuId) {
        return service.spuImageList(spuId);
    }


}
