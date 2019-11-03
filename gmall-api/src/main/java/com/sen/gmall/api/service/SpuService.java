package com.sen.gmall.api.service;

import com.sen.gmall.api.beans.PmsProductImage;
import com.sen.gmall.api.beans.PmsProductInfo;
import com.sen.gmall.api.beans.PmsProductSaleAttr;

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
