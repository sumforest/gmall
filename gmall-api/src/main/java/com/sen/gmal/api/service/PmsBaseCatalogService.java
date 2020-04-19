package com.sen.gmal.api.service;

import com.sen.gmal.api.beans.PmsBaseCatalog1;
import com.sen.gmal.api.beans.PmsBaseCatalog2;
import com.sen.gmal.api.beans.PmsBaseCatalog3;

import java.util.List;

/**
 * @Author: Sen
 * @Date: 2019/11/2 16:24
 * @Description:
 */
public interface PmsBaseCatalogService {

    List<PmsBaseCatalog1> getCatalog1();

    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);

    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
}
