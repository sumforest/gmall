package com.sen.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.sen.gmal.api.beans.OmsOrder;
import com.sen.gmal.api.beans.PaymentInfo;
import com.sen.gmal.api.service.OmsOrderService;
import com.sen.gmal.api.service.PaymentService;
import com.sen.gmall.payment.config.AlipayConfig;
import com.sen.gmall.web.annotations.LoginRequire;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Sen
 * @Date: 2019/11/10 23:02
 * @Description:
 */
@Controller
public class PaymentController {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private PaymentService paymentService;



    @Reference
    private OmsOrderService orderService;

    @GetMapping("/index")
    @LoginRequire
    public String toPaymentIndex(String outTradeNo, String totalAmount, HttpServletRequest request, ModelMap modelMap) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        modelMap.put("orderId", outTradeNo);
        modelMap.put("totalAmount", totalAmount);
        modelMap.put("nickName", nickname);
        return "index";
    }

    @PostMapping("/alipay/submit")
    @LoginRequire
    @ResponseBody
    public String alipaySubmit(String outTradeNo, BigDecimal totalAmount) {
        //创建请api对应的request
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(AlipayConfig.return_payment_url);
        request.setNotifyUrl(AlipayConfig.notify_payment_url);
        //封装参数
        Map<String,String> map = new HashMap<>();
        map.put("out_trade_no", outTradeNo);
        map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        map.put("total_amount", "0.01");
        map.put("subject", "尚硅谷谷粒商城iPhone12 512G");
        request.setBizContent(JSON.toJSONString(map));

        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //生成并保存用户的支付信息
        OmsOrder order = orderService.getOrderByOutTradeNo(outTradeNo);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderSn(order.getOrderSn());
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setTotalAmount(totalAmount);
        paymentInfo.setPaymentStatus("未付款");
        paymentInfo.setSubject("尚硅谷谷粒商城iPhone12 512G");
        paymentService.addPayment(paymentInfo);
        //进入支付页面前发送一个延时队列到mq中，查询支付结果
        paymentService.sendDelayMessageCheckPayResult(outTradeNo,5);

        return form;
    }

    @PostMapping("/mx/submit")
    @LoginRequire
    public String mxSubmit(String outTradeNo, String totalAmount) {

        return null;
    }

    @GetMapping("/alipay/callback/return")
    @LoginRequire
    public String AlipayCallBackReturn(HttpServletRequest request) {
        //更新用户的支付信息并保存
        String trade_no = request.getParameter("trade_no");
        String sign = request.getParameter("sign");
        String call_back_content = request.getQueryString();

        //验签通过
        if (StringUtils.isNotBlank(sign)) {
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setAlipayTradeNo(trade_no);
            paymentInfo.setCallbackContent(call_back_content);
            paymentInfo.setCallbackTime(new Date());
            paymentInfo.setPaymentStatus("已付款");
            //更新订单信息
            paymentService.updatePayment(paymentInfo);

        } else {
            return "fail";
        }

        return "finish";
    }
}
