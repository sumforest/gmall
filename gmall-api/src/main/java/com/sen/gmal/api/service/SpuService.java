package com.sen.gmal.api.service;

import com.sen.gmal.api.beans.PmsProductImage;
import com.sen.gmal.api.beans.PmsProductInfo;
import com.sen.gmal.api.beans.PmsProductSaleAttr;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/2 22:06
 * @Description:
 */
public interface SpuService {

    List<PmsProductInfo> spuList(String catalog3Id);

    void saverSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);

    List<PmsProductImage> spuImageList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String skuId, String spuId);
}
