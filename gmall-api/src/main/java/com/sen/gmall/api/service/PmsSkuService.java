package com.sen.gmall.api.service;

import com.sen.gmall.api.beans.PmsSkuInfo;

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
}
