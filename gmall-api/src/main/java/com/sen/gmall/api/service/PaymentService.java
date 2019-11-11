package com.sen.gmall.api.service;

import com.sen.gmall.api.beans.PaymentInfo;

import java.util.Map;

/**
 * @Auther: Sen
 * @Date: 2019/11/11 16:24
 * @Description:
 */
public interface PaymentService {
    void addPayment(PaymentInfo paymentInfo);

    void updatePayment(PaymentInfo paymentInfo);

    void sendDelayMessageCheckPayResult(String outTradeNo);

    Map<String, Object> checkPaymentStatus(String outTradeNo);
}
