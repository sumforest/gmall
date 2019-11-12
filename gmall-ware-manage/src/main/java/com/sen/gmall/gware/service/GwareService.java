package com.sen.gmall.gware.service;

import com.sen.gmall.gware.bean.WmsWareInfo;
import com.sen.gmall.gware.bean.WmsWareOrderTask;
import com.sen.gmall.gware.bean.WmsWareSku;

import java.util.List;
import java.util.Map;

/**
 * @param
 * @return
 */
public interface GwareService {
    Integer getStockBySkuId(String skuid);

    boolean hasStockBySkuId(String skuid, Integer num);

    List<WmsWareInfo> getWareInfoBySkuid(String skuid);

    void addWareInfo();

    Map<String, List<String>> getWareSkuMap(List<String> skuIdlist);

    void addWareSku(WmsWareSku wmsWareSku);

    void deliveryStock(WmsWareOrderTask taskExample);

    WmsWareOrderTask saveWareOrderTask(WmsWareOrderTask wmsWareOrderTask);

    List<WmsWareOrderTask> checkOrderSplit(WmsWareOrderTask wmsWareOrderTask);

    void lockStock(WmsWareOrderTask wmsWareOrderTask);

    List<WmsWareOrderTask> getWareOrderTaskList(WmsWareOrderTask wmsWareOrderTask);

    List<WmsWareSku> getWareSkuList();

    List<WmsWareInfo> getWareInfoList();
}
