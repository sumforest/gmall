package com.sen.gmall.api.service;

import com.sen.gmall.api.beans.PmsSearchParam;
import com.sen.gmall.api.beans.PmsSearchSkuInfo;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/6 18:45
 * @Description:
 */
public interface SearchService {
    List<PmsSearchSkuInfo> searchPmsSkuInfo(PmsSearchParam pmsSearchParam);
}
