package com.sen.gmall.api.service;

import com.sen.gmall.api.beans.OmsOrder;

/**
 * @Auther: Sen
 * @Date: 2019/11/10 15:20
 * @Description:
 */
public interface OmsOrderService {
    String checkTradeCode(String memberId, String tradeCode);

    Object createTradeCode(String memberId);

    void addOmsOrder(OmsOrder omsOrder);
}
