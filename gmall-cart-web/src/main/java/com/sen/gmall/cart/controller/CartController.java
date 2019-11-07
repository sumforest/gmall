package com.sen.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.sen.gmall.api.beans.OmsCartItem;
import com.sen.gmall.api.beans.PmsSkuInfo;
import com.sen.gmall.api.service.OmsCartItemService;
import com.sen.gmall.api.service.PmsSkuService;
import com.sen.gmall.web.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/7 17:52
 * @Description:
 */
@Controller
public class CartController {

    @Reference
    private PmsSkuService skuService;

    @Reference
    private OmsCartItemService cartService;

    @RequestMapping("/addToCart")
    public String addToCart(String skuId, String quantity,
                            HttpServletRequest request, HttpServletResponse response) {
        //从数据中查询skuInfo
        PmsSkuInfo skuInfo = skuService.getSkuInfoById(skuId);

        //封装购物车对象
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setProductSkuId(skuInfo.getId());
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductPic(skuInfo.getSkuImageList().get(0).getImgUrl());
        omsCartItem.setProductId(skuInfo.getSpuId());
        omsCartItem.setQuantity(Integer.parseInt(quantity));
        omsCartItem.setProductName(skuInfo.getSkuName());

        //假设用户未登录
        String memberId = "1";
        if (StringUtils.isBlank(memberId)) {
            //封装购物车cookie
            List<OmsCartItem> cartItems = new ArrayList<>();
            cartItems.add(omsCartItem);
            String cookieValue = CookieUtil.getCookieValue(request, "cartItems", true);
            //cookie不为空
            if (StringUtils.isNotBlank(cookieValue)) {
                cartItems = JSON.parseArray(cookieValue, OmsCartItem.class);

                //检查新添加的商品是否已存在
                boolean isExist = checkIsExist(omsCartItem, cartItems);
                //如果新增的购物车商品已存在，修改已存在购物车的值
                if (isExist) {
                    for (OmsCartItem cookieCartItem : cartItems) {
                        if (cookieCartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            cookieCartItem.setQuantity(cookieCartItem.getQuantity() + omsCartItem.getQuantity());
                        }
                    }
                } else {
                    cartItems.add(omsCartItem);
                }
                //覆盖原cookie
                CookieUtil.setCookie(request, response, "cartItems",
                        JSON.toJSONString(cartItems
                        ), 3600 * 3, true);

                //cookie为空直接添加
            } else {
                CookieUtil.setCookie(request, response, "cartItems",
                        JSON.toJSONString(cartItems), 3600 * 3, true);
            }

            //用户已登录
        } else {
            //从db中查询
            OmsCartItem cartItemFormDb = cartService.getCartItemByMemberIdAndSkuId(skuId, memberId);
            //用户没有添加过当前商品
            if (cartItemFormDb == null) {
                //保存新商品到用户的购物车
                omsCartItem.setMemberId(memberId);
                cartService.add(omsCartItem);

                //已添加过当前商品
            } else {
                cartItemFormDb.setQuantity(cartItemFormDb.getQuantity() + omsCartItem.getQuantity());
                //更新db
                cartService.update(cartItemFormDb);
            }

            //刷新缓存
            cartService.flushCache(memberId);
        }

        return "redirect:/success.html";
    }

    @GetMapping("/cartList")
    public String toCartList(HttpServletRequest request, ModelMap modelMap) {

        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String memberId = "1";
        //用户已登录
        if (StringUtils.isNotBlank(memberId)) {
            //从缓存中获取数据
            omsCartItems = cartService.getCartList(memberId);

        //用户未登录
        } else {
            //从cookie中获取购物车数据
            String cookieValue = CookieUtil.getCookieValue(request, "cartItems", true);
            if (StringUtils.isNotBlank(cookieValue)) {
                omsCartItems = JSON.parseArray(cookieValue, OmsCartItem.class);
            }
        }

        //设置小计价格
        if (omsCartItems != null && omsCartItems.size() > 0) {
            for (OmsCartItem omsCartItem : omsCartItems) {
                omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity())));
            }
        }
        modelMap.put("cartList", omsCartItems);
        return "cartList";
    }

    @PostMapping("checkCart")
    public String checkCart(OmsCartItem omsCartItem,ModelMap modelMap) {
        String memberId = "1";
        omsCartItem.setMemberId(memberId);
        //修改更新数据库
        cartService.checkCart(omsCartItem);
        //从缓存中获取数据
        List<OmsCartItem> cartList = cartService.getCartList(omsCartItem.getMemberId());

        modelMap.put("cartList", cartList);
        return "cartListInner";
    }
    /**
     * 检查当前添加到购物车的商品是否已经存在
     *
     * @param omsCartItem 当前购物车里面的商品
     * @param cartItems   缓存到cookie的所有商品
     * @return true 已存在
     */
    private boolean checkIsExist(OmsCartItem omsCartItem, List<OmsCartItem> cartItems
    ) {

        boolean isExist = false;
        for (OmsCartItem cookieCartItem : cartItems
        ) {
            if (cookieCartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                isExist = true;
            }
        }
        return isExist;
    }
}
