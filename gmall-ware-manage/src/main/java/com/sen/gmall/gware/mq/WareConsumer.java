package com.sen.gmall.gware.mq;

import com.alibaba.fastjson.JSON;

import com.sen.gmall.gware.bean.OmsOrder;
import com.sen.gmall.gware.bean.OmsOrderItem;
import com.sen.gmall.gware.bean.WmsWareOrderTask;
import com.sen.gmall.gware.bean.WmsWareOrderTaskDetail;
import com.sen.gmall.gware.enums.TaskStatus;
import com.sen.gmall.gware.mapper.WareOrderTaskDetailMapper;
import com.sen.gmall.gware.mapper.WareOrderTaskMapper;
import com.sen.gmall.gware.mapper.WareSkuMapper;
import com.sen.gmall.gware.service.GwareService;
import com.sen.gmall.gware.util.ActiveMQUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;
import java.util.*;

/**
 * @param
 * @return
 */
@Component
public class WareConsumer {

    @Autowired
    WareOrderTaskMapper wareOrderTaskMapper;

    @Autowired
    WareOrderTaskDetailMapper wareOrderTaskDetailMapper;

    @Autowired
    WareSkuMapper wareSkuMapper;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    GwareService gwareService;

    @JmsListener(destination = "ORDER_PAY_QUEUE", containerFactory = "jmsQueueListener")
    public void receiveOrder(MapMessage mapMessage) throws JMSException {
        String orderJson = mapMessage.getString("order");

        /***orderJson = "{"autoConfirmDay":0,"billType":0,"confirmStatus":0,"createTime":1573573413000,"deleteStatus":0,"deliveryTime":1573659813000,"growth":0,"id":"65","integration":0,"memberId":"1","memberUsername":"windir","orderSn":"gmall157357341353320191112234333","orderType":0,"payAmount":203664.00,"payType":0,"receiverCity":"深圳市","receiverDetailAddress":"东晓街道","receiverName":"大梨","receiverPhone":"18033441849","receiverPostCode":"518000","receiverProvince":"广东省","receiverRegion":"福田区","sourceType":0,"status":1,"totalAmount":203664.00,"useIntegration":0}"
         * 转化并保存订单对象
         */
        OmsOrder orderInfo = JSON.parseObject(orderJson, OmsOrder.class);

        // 将order订单对象转为订单任务对象
        WmsWareOrderTask wmsWareOrderTask = new WmsWareOrderTask();
        wmsWareOrderTask.setConsignee(orderInfo.getReceiverName());
        wmsWareOrderTask.setConsigneeTel(orderInfo.getReceiverPhone());
        wmsWareOrderTask.setCreateTime(new Date());
        wmsWareOrderTask.setDeliveryAddress(orderInfo.getReceiverDetailAddress());
        wmsWareOrderTask.setOrderId(orderInfo.getId());
        ArrayList<WmsWareOrderTaskDetail> wmsWareOrderTaskDetails = new ArrayList<>();

        // 打开订单的商品集合
        List<OmsOrderItem> orderDetailList = orderInfo.getOmsOrderItems();
        for (OmsOrderItem orderDetail : orderDetailList) {
            WmsWareOrderTaskDetail wmsWareOrderTaskDetail = new WmsWareOrderTaskDetail();

            wmsWareOrderTaskDetail.setSkuId(orderDetail.getProductSkuId());
            wmsWareOrderTaskDetail.setSkuName(orderDetail.getProductName());
            wmsWareOrderTaskDetail.setSkuNum(orderDetail.getProductQuantity());
            wmsWareOrderTaskDetails.add(wmsWareOrderTaskDetail);

        }
        wmsWareOrderTask.setDetails(wmsWareOrderTaskDetails);
        wmsWareOrderTask.setTaskStatus(TaskStatus.PAID);
        gwareService.saveWareOrderTask(wmsWareOrderTask);


        // 检查该交易的商品是否有拆单需求
        List<WmsWareOrderTask> wareSubOrderTaskList = gwareService.checkOrderSplit(wmsWareOrderTask);// 检查拆单

        // 库存削减
        if (wareSubOrderTaskList != null && wareSubOrderTaskList.size() >= 2) {
            for (WmsWareOrderTask orderTask : wareSubOrderTaskList) {
                gwareService.lockStock(orderTask);
            }
        } else {
            gwareService.lockStock(wmsWareOrderTask);
        }


    }

}
