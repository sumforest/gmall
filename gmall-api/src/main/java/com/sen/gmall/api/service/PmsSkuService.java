package com.sen.gmall.api.service;

import com.sen.gmall.api.beans.PmsSkuInfo;

/**
 * @Auther: Sen
 * @Date: 2019/11/3 18:25
 * @Description:
 */
public interface PmsSkuService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuInfoById(String skuId);
}
