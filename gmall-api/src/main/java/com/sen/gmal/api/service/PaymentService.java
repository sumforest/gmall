package com.sen.gmal.api.service;

import com.sen.gmal.api.beans.PaymentInfo;

import java.util.Map;

/**
 * @Author: Sen
 * @Date: 2019/11/11 16:24
 * @Description:
 */
public interface PaymentService {
    void addPayment(PaymentInfo paymentInfo);

    void updatePayment(PaymentInfo paymentInfo);

    void sendDelayMessageCheckPayResult(String outTradeNo,int count);

    Map<String, Object> checkPaymentStatus(String outTradeNo);
}
