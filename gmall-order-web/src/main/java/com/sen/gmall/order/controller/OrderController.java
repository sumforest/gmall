package com.sen.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sen.gmall.api.beans.OmsCartItem;
import com.sen.gmall.api.beans.OmsOrder;
import com.sen.gmall.api.beans.OmsOrderItem;
import com.sen.gmall.api.beans.UmsMemberReceiveAddress;
import com.sen.gmall.api.service.OmsCartItemService;
import com.sen.gmall.api.service.OmsOrderService;
import com.sen.gmall.api.service.PmsSkuService;
import com.sen.gmall.api.service.UmsMemberReciveAddressService;
import com.sen.gmall.web.annotations.LoginRequire;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/10 00:17
 * @Description:
 */
@Controller
public class OrderController {
    @Reference
     private UmsMemberReciveAddressService addressService;

    @Reference
    private OmsCartItemService cartItemService;

    @Reference
    private OmsOrderService orderService;

    @Reference
    private PmsSkuService skuService;


    @PostMapping("/submitOrder")
    @LoginRequire
    public ModelAndView submitOrder(String addressId, String tradeCode, HttpServletRequest request) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        String success = orderService.checkTradeCode(memberId, tradeCode);
        if (StringUtils.isNotBlank(success) && "success".equals(success)) {
            //通过用户Id查询购物车
            List<OmsCartItem> cartList = cartItemService.getCartList(memberId);
            //把购物车详情对象转换成订单详情对象
            List<OmsOrderItem> orderItems = new ArrayList<>();

            //外部订单编号
            String sn = "gmall" + System.currentTimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String format1 = format.format(new Date());
            sn = sn + format1;

            for (OmsCartItem omsCartItem : cartList) {
                if ("1".equals(omsCartItem.getIsCheck())) {
                    //检查商品价格
                    boolean isSuccess = skuService.checkPrice(omsCartItem.getProductSkuId(),omsCartItem.getPrice());
                    if (!isSuccess) {
                        return new ModelAndView("/tradeFail");
                    }
                    //检查商品库存

                    //封装订单详情对象
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setOrderSn(sn);
                    omsOrderItem.setProductName(omsCartItem.getProductName());
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setRealAmount(omsCartItem.getTotalPrice());

                    orderItems.add(omsOrderItem);
                }
            }
            // 封装订单对象
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setOrderItems(orderItems);
            omsOrder.setCreateTime(new Date());
            omsOrder.setAutoConfirmDay(7);
            omsOrder.setMemberId(memberId);
            //总价
            BigDecimal totalPrice = caculate(orderItems);
            omsOrder.setTotalAmount(totalPrice);
            omsOrder.setMemberUsername(nickname);
            omsOrder.setOrderSn(sn);
            omsOrder.setPayAmount(totalPrice);
            //配送时间
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            omsOrder.setDeliveryTime(calendar.getTime());
            UmsMemberReceiveAddress address = addressService.getDeliveryAddressByAddressId(addressId);
            omsOrder.setReceiverCity(address.getCity());
            omsOrder.setReceiverDetailAddress(address.getDetailAddress());
            omsOrder.setReceiverName(address.getName());
            omsOrder.setReceiverPhone(address.getPhoneNumber());
            omsOrder.setReceiverPostCode(address.getPostCode());
            omsOrder.setReceiverProvince(address.getProvince());
            omsOrder.setReceiverRegion(address.getRegion());
            omsOrder.setStatus(0);
            //将订单和订单详情写入数据库，删除购物车里面相应的商品
            orderService.addOmsOrder(omsOrder);
            //重定向到支付系统
            ModelAndView modelAndView = new ModelAndView("redirect:http://payment.gmall.com:8087/index");
            modelAndView.addObject("outTradeNo", sn);
            modelAndView.addObject("totalAmount", totalPrice);
            return modelAndView;
        }
        return new ModelAndView("/tradeFail");
    }

    @GetMapping("/toTrade")
    @LoginRequire
    public String toTrade(HttpServletRequest request, ModelMap modelMap) {

        String memberId = (String) request.getAttribute("memberId");
        //查询用户的地址
        List<UmsMemberReceiveAddress> addresses = addressService.selectByUmsMemberId(memberId);

        //查询购物车
        List<OmsCartItem> cartList = cartItemService.getCartList(memberId);
        List<OmsOrderItem> orderItems = new ArrayList<>();
        //把购车的品转换成订单实体
        for (OmsCartItem cartItem : cartList) {
            OmsOrderItem omsOrderItem = new OmsOrderItem();
            omsOrderItem.setProductName(cartItem.getProductName());
            omsOrderItem.setProductPrice(cartItem.getPrice());
            omsOrderItem.setProductPic(cartItem.getProductPic());
            omsOrderItem.setProductQuantity(cartItem.getQuantity());
            orderItems.add(omsOrderItem);
        }

        //生成交易码
        modelMap.put("tradeCode", orderService.createTradeCode(memberId));

        modelMap.put("orderDetailList", orderItems);
        modelMap.put("userAddressList", addresses);
        modelMap.put("totalAmount", caculate(orderItems));
        return "trade";
    }

    @GetMapping("/tradeFail")
    public String toTradeFail() {
        return "tradeFail";
    }

    private BigDecimal caculate(List<OmsOrderItem> orderItems) {
        //计订单总价
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsOrderItem orderItem : orderItems) {
            BigDecimal productPrice = orderItem.getProductPrice();
            BigDecimal quantity = new BigDecimal(orderItem.getProductQuantity());
            totalAmount = productPrice.multiply(quantity).add(totalAmount);
        }
        return totalAmount;
    }
}
