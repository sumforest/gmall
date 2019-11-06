package com.sen.gmall.api.service;

import com.sen.gmall.api.beans.PmsBaseAttrInfo;
import com.sen.gmall.api.beans.PmsBaseAttrValue;
import com.sen.gmall.api.beans.PmsBaseSaleAttr;
import com.sen.gmall.api.beans.PmsSearchSkuInfo;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/2 17:23
 * @Description:
 */
public interface PmsBaseAttrService {
    List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo attrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> getBaseSaleAttrList();

    List<PmsBaseAttrInfo> getSearchAttrAndAttrValues(List<PmsSearchSkuInfo> pmsSearchSkuInfos);
}
