package com.sen.gmall.order.mq;

import com.sen.gmall.api.service.OmsOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * @Auther: Sen
 * @Date: 2019/11/12 00:35
 * @Description:
 */
@Component
public class MqOrderComsumer {

    @Autowired
    private OmsOrderService orderService;

    @JmsListener(destination = "PAYMENT_SUCCESS_QUEUE",containerFactory = "jmsQueueListener")
    public void consumePaymentResult(MapMessage mapMessage) throws JMSException {
        String outTradeNo = mapMessage.getString("outTradeNo");
        if (StringUtils.isNotBlank(outTradeNo)) {
            orderService.updateOrder(outTradeNo);
        }
    }
}
