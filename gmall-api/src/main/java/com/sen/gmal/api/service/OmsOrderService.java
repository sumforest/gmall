package com.sen.gmal.api.service;

import com.sen.gmal.api.beans.OmsOrder;

/**
 * @Author: Sen
 * @Date: 2019/11/10 15:20
 * @Description:
 */
public interface OmsOrderService {
    String checkTradeCode(String memberId, String tradeCode);

    Object createTradeCode(String memberId);

    void addOmsOrder(OmsOrder omsOrder);

    OmsOrder getOrderByOutTradeNo(String outTradeNo);

    void updateOrder(String outTradeNo);
}
