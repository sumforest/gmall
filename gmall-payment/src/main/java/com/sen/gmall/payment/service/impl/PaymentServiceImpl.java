package com.sen.gmall.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.sen.gmal.api.beans.PaymentInfo;
import com.sen.gmal.api.service.PaymentService;
import com.sen.gmall.payment.mapper.PaymentMapper;
import com.sen.gmall.util.ActiveMQUtil;
import com.sen.gmall.util.ProductMessageUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Sen
 * @Date: 2019/11/11 16:28
 * @Description:
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Autowired
    private AlipayClient alipayClient;

    @Override
    public void addPayment(PaymentInfo paymentInfo) {
        paymentMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {
        //更新前做幂等性检查
        PaymentInfo paymentInfoParam = new PaymentInfo();
        paymentInfoParam.setOrderSn(paymentInfo.getAlipayTradeNo());
        PaymentInfo resultPaymentInfo = paymentMapper.selectOne(paymentInfoParam);
        if (resultPaymentInfo != null && "已支付".equals(resultPaymentInfo.getPaymentStatus())) {
            return;
        }

        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderSn", paymentInfo.getOrderSn());

        //更新订单信息，库存系统，物流系统(分布式事务)
        Session session = null;

        try {
            paymentMapper.updateByExampleSelective(paymentInfo, example);
            //消息的参数
            MapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("outTradeNo", paymentInfo.getOrderSn());
            //发送消息调用订单服务更新订单状态
            session = ProductMessageUtil.sendMessage(activeMQUtil, mapMessage, "PAYMENT_SUCCESS_QUEUE");

        } catch (Exception e) {
            e.printStackTrace();
            //事务滚
            try {
                if (session != null) session.rollback();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void sendDelayMessageCheckPayResult(String outTradeNo,int count) {
        MapMessage mapMessage = new ActiveMQMapMessage();
        Session session = null;
        try {
            mapMessage.setString("outTradeNo", outTradeNo);
            mapMessage.setInt("count", count);
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000 * 10);
            session = ProductMessageUtil.sendMessage(activeMQUtil, mapMessage, "PAYMENT_CHECK_QUEUE");
        } catch (JMSException e) {
            e.printStackTrace();

            if (session != null) {
                try {
                    //回滚
                    session.rollback();
                } catch (JMSException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    @Override
    public Map<String, Object> checkPaymentStatus(String outTradeNo) {
        Map<String,Object> resultMap = new HashMap<>();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("out_trade_no", outTradeNo);
        request.setBizContent(JSON.toJSONString(paramMap));
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                //调用成功
                resultMap.put("trade_status", response.getTradeStatus());
                resultMap.put("out_trade_no", response.getOutTradeNo());
                resultMap.put("trade_no", response.getTradeNo());
                resultMap.put("call_back_content", response.getMsg());
            } else {
                // 调用失败
                System.out.println("有可能交易未创建，调用失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

}
