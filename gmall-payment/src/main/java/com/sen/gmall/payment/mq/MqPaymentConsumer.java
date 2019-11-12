package com.sen.gmall.payment.mq;

import com.sen.gmal.api.beans.PaymentInfo;
import com.sen.gmal.api.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Date;
import java.util.Map;

/**
 * @Auther: Sen
 * @Date: 2019/11/12 03:04
 * @Description: 检查支付宝支付状态延时队列消费者
 */
@Component
public class MqPaymentConsumer {

    @Autowired
    private PaymentService paymentService;

    @JmsListener(destination = "PAYMENT_CHECK_QUEUE", containerFactory = "jmsQueueListener")
    public void checkAliPayStatus(MapMessage mapMessage) throws JMSException {
        String outTradeNo = mapMessage.getString("outTradeNo");
        int count = mapMessage.getInt("count");

        //调用支付报查询支付状态api
        Map<String, Object> resultMap = paymentService.checkPaymentStatus(outTradeNo);
        if (resultMap != null && !resultMap.isEmpty()) {
            String trade_status = (String) resultMap.get("trade_status");
            String trade_no = (String) resultMap.get("trade_no");
            String call_back_content = (String) resultMap.get("call_back_content");
            String out_trade_no = (String) resultMap.get("out_trade_no");
            if ("TRADE_SUCCESS".equals(trade_status)) {
                System.out.println("支付成功！！！");
                //支付成功
                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.setAlipayTradeNo(trade_no);
                paymentInfo.setOrderSn(out_trade_no);
                paymentInfo.setCallbackContent(call_back_content);
                paymentInfo.setCallbackTime(new Date());
                paymentInfo.setPaymentStatus("已付款");
                paymentService.updatePayment(paymentInfo);
                return;
            }
        }
        if (count > 0) {
            //支付接口调用失败，重新发起查询
            count--;
            paymentService.sendDelayMessageCheckPayResult(outTradeNo, count);
            System.out.println("支付接口调用失败，剩余次数：" + count + ",10s后重新发起调用");
        } else {
            System.out.println("检查次数耗尽，借宿本次交易检查");
        }
    }
}
