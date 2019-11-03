package com.sen.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sen.gmall.api.beans.PmsBaseCatalog1;
import com.sen.gmall.api.beans.PmsBaseCatalog2;
import com.sen.gmall.api.beans.PmsBaseCatalog3;
import com.sen.gmall.api.service.PmsBaseCatalogService;
import com.sen.gmall.manage.mapper.PmsBaseCatalog1Mapper;
import com.sen.gmall.manage.mapper.PmsBaseCatalog2Mapper;
import com.sen.gmall.manage.mapper.PmsBaseCatalog3Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/2 16:27
 * @Description:
 */
@Service
public class PmsBaseCatalogServiceImpl implements PmsBaseCatalogService {

    @Autowired
    private PmsBaseCatalog1Mapper mapper1;

    @Autowired
    private PmsBaseCatalog2Mapper mapper2;

    @Autowired
    private PmsBaseCatalog3Mapper mapper3;

    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        List<PmsBaseCatalog1> pmsBaseCatalog1s = mapper1.selectAll();
        return pmsBaseCatalog1s;
    }

    @Override
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {

        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setCatalog1Id(catalog1Id);
        List<PmsBaseCatalog2> catalog2s = mapper2.select(pmsBaseCatalog2);

        return catalog2s;
    }

    @Override
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {

        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
        pmsBaseCatalog3.setCatalog2Id(catalog2Id);
        List<PmsBaseCatalog3> catalog3s = mapper3.select(pmsBaseCatalog3);
        return catalog3s;
    }
}
