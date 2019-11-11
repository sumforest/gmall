package com.sen.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sen.gmall.api.beans.OmsOrder;
import com.sen.gmall.api.beans.OmsOrderItem;
import com.sen.gmall.api.service.OmsOrderService;
import com.sen.gmall.order.mapper.OmsOrderItemMapper;
import com.sen.gmall.order.mapper.OmsOrderMapper;
import com.sen.gmall.util.ActiveMQUtil;
import com.sen.gmall.util.ProductMessageUtil;
import com.sen.gmall.util.RedisUtil;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import java.util.Collections;
import java.util.UUID;

/**
 * @Auther: Sen
 * @Date: 2019/11/10 15:21
 * @Description:
 */
@Service
public class OmsOrderServiceImpl implements OmsOrderService {

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsOrderItemMapper orderItemMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Override
    public String checkTradeCode(String memberId, String tradeCode) {
        String success = "fail";

        try (final Jedis jedis = redisUtil.getJedis()) {
            String tradeKey = "user:" + memberId + ":tradeCode";
            //使用lua脚本，使redis查询和删除数据操作具有原子性，防止订单并发攻击
            String lua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long status = (Long) jedis.eval(lua, Collections.singletonList(tradeKey), Collections.singletonList(tradeCode));
            if (status == 1) {
                success = "success";
                jedis.del(tradeKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    @Override
    public Object createTradeCode(String memberId) {
        String tradeCode = UUID.randomUUID().toString();
        try (Jedis jedis = redisUtil.getJedis()) {
            String tradeKey = "user:" + memberId + ":tradeCode";
            jedis.setex(tradeKey, 60 * 15, tradeCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tradeCode;
    }

    @Override
    public void addOmsOrder(OmsOrder omsOrder) {
        orderMapper.insertSelective(omsOrder);
        //保存商品详情
        for (OmsOrderItem orderItem : omsOrder.getOrderItems()) {
            orderItem.setOrderId(omsOrder.getId());
            orderItemMapper.insertSelective(orderItem);
            //删除购物车商品详情
        }
    }

    @Override
    public OmsOrder getOrderByOutTradeNo(String outTradeNo) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(outTradeNo);
        return orderMapper.selectOne(omsOrder);
    }

    @Override
    public void updateOrder(String outTradeNo) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(outTradeNo);
        omsOrder.setStatus(1);
        Example example = new Example(OmsOrder.class);
        example.createCriteria().andEqualTo("orderSn", omsOrder.getOrderSn());
        Session session = null;
        try {
            orderMapper.updateByExampleSelective(omsOrder, example);
            //通知库存系统
            MapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("test", "test");

            session = ProductMessageUtil.sendMessage(activeMQUtil, mapMessage, "ORDER_PAY_QUEUE");
        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (session!= null) session.rollback();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        }
    }
}
