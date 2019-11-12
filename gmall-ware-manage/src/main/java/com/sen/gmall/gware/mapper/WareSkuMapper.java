package com.sen.gmall.gware.mapper;

import com.sen.gmall.gware.bean.WmsWareSku;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @param
 * @return
 */
public interface WareSkuMapper extends Mapper<WmsWareSku> {

     Integer selectStockBySkuid(String skuid);

     int incrStockLocked(WmsWareSku wmsWareSku);

     int selectStockBySkuidForUpdate(WmsWareSku wmsWareSku);

     int  deliveryStock(WmsWareSku wmsWareSku);

     List<WmsWareSku> selectWareSkuAll();
}
