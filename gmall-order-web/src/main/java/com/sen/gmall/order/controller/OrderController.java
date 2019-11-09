package com.sen.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sen.gmall.api.beans.OmsCartItem;
import com.sen.gmall.api.beans.OmsOrderItem;
import com.sen.gmall.api.beans.UmsMemberReceiveAddress;
import com.sen.gmall.api.service.OmsCartItemService;
import com.sen.gmall.api.service.UmsMemberReciveAddressService;
import com.sen.gmall.api.service.UmsMemberService;
import com.sen.gmall.web.annotations.LoginRequire;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    @PostMapping("/submitOrder")
    @ResponseBody
    public String submitOrder(String addressId,HttpServletRequest request) {
        String memberId = (String) request.getAttribute("memberId");
        //通过用户Id查询购物车

        //查询库存

        //将订单和订单详情写入数据库，删除购物车里面相应的商品

        //重定向到支付系统
        return null;
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
            omsOrderItem.setProductPrice(cartItem.getPrice().toString());
            omsOrderItem.setProductPic(cartItem.getProductPic());
            omsOrderItem.setProductQuantity(cartItem.getQuantity());
            orderItems.add(omsOrderItem);
        }
        //计订单总价
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsOrderItem orderItem : orderItems) {
            BigDecimal productPrice = new BigDecimal(orderItem.getProductPrice());
            BigDecimal quantity = new BigDecimal(orderItem.getProductQuantity());
            totalAmount = productPrice.multiply(quantity).add(totalAmount);
        }
        modelMap.put("orderDetailList", orderItems);
        modelMap.put("userAddressList", addresses);
        modelMap.put("totalAmount", totalAmount);
        return "trade";
    }
}
