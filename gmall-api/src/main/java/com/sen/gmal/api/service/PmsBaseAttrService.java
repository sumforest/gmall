package com.sen.gmal.api.service;

import com.sen.gmal.api.beans.PmsBaseAttrInfo;
import com.sen.gmal.api.beans.PmsBaseAttrValue;
import com.sen.gmal.api.beans.PmsBaseSaleAttr;
import com.sen.gmal.api.beans.PmsSearchSkuInfo;

import java.util.List;

/**
 * @Author: Sen
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
