package com.sen.gmal.api.service;

import com.sen.gmal.api.beans.PmsSkuInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Auther: Sen
 * @Date: 2019/11/3 18:25
 * @Description:
 */
public interface PmsSkuService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuInfoById(String skuId);

    Map<String,String> getSkuInfoAndSaleAttrValues(String spuId);

    List<PmsSkuInfo> getAll();

    boolean checkPrice(String productSkuId, BigDecimal price);
}
