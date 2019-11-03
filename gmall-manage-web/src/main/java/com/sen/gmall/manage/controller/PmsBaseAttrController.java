package com.sen.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sen.gmall.api.beans.PmsBaseAttrInfo;
import com.sen.gmall.api.beans.PmsBaseAttrValue;
import com.sen.gmall.api.beans.PmsBaseSaleAttr;
import com.sen.gmall.api.service.PmsBaseAttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.awt.SunHints;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/2 17:20
 * @Description:
 */
@Controller
@CrossOrigin
@ResponseBody
public class PmsBaseAttrController {

    @Reference
    private PmsBaseAttrService service;

    @GetMapping("/attrInfoList")
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id) {
        return service.getAttrInfoList(catalog3Id);
    }

    @RequestMapping("/saveAttrInfo")
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo attrInfo) {
        return service.saveAttrInfo(attrInfo);
    }

    @PostMapping("/getAttrValueList")
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){
        return service.getAttrValueList(attrId);
    }

    @PostMapping("/baseSaleAttrList")
    public List<PmsBaseSaleAttr> getBaseSaleAttrList() {
        return service.getBaseSaleAttrList();
    }
}
