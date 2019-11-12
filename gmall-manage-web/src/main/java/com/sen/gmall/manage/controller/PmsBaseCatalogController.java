package com.sen.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sen.gmal.api.beans.PmsBaseCatalog1;
import com.sen.gmal.api.beans.PmsBaseCatalog2;
import com.sen.gmal.api.beans.PmsBaseCatalog3;
import com.sen.gmal.api.service.PmsBaseCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/2 16:31
 * @Description:
 */
@Controller
@CrossOrigin
@ResponseBody
public class PmsBaseCatalogController {

    @Reference
    private PmsBaseCatalogService service;

    @PostMapping("/getCatalog1")
    public List<PmsBaseCatalog1> getCatalog1() {
        return service.getCatalog1();
    }

    @PostMapping("/getCatalog2")
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {
        return service.getCatalog2(catalog1Id);
    }

    @PostMapping("/getCatalog3")
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {
        return service.getCatalog3(catalog2Id);
    }
}
