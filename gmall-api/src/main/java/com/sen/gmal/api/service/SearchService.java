package com.sen.gmal.api.service;

import com.sen.gmal.api.beans.PmsSearchSkuInfo;
import com.sen.gmal.api.beans.PmsSearchParam;

import java.util.List;

/**
 * @Author: Sen
 * @Date: 2019/11/6 18:45
 * @Description:
 */
public interface SearchService {
    List<PmsSearchSkuInfo> searchPmsSkuInfo(PmsSearchParam pmsSearchParam);
}
