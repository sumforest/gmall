package com.sen.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.sen.gmal.api.beans.OmsCartItem;
import com.sen.gmal.api.beans.PmsSkuInfo;
import com.sen.gmal.api.service.OmsCartItemService;
import com.sen.gmal.api.service.PmsSkuService;
import com.sen.gmall.web.util.CookieUtil;
import com.sen.gmall.web.annotations.LoginRequire;
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
 * @Author: Sen
 * @Date: 2019/11/7 17:52
 * @Description: 购物车
 */
@Controller
public class CartController {

    @Reference
    private PmsSkuService skuService;

    @Reference
    private OmsCartItemService cartService;

    /**
     * 添加购物车
     *
     * @param skuId    商品库存id
     * @param quantity 数量
     * @param request  请求
     * @param response 响应
     * @return json信息
     */
    @RequestMapping("/addToCart")
    @LoginRequire(loginSuccess = false)
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
        String memberId = (String) request.getAttribute("memberId");
        ;
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
                //如果新增的购物车商品已存在，修改已存在商品的数量
                if (isExist) {
                    for (OmsCartItem cookieCartItem : cartItems) {
                        if (cookieCartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            cookieCartItem.setQuantity(cookieCartItem.getQuantity() + omsCartItem.getQuantity());
                        }
                    }
                } else {
                    cartItems.add(omsCartItem);
                }
            }
            //覆盖原cookie
            CookieUtil.setCookie(request, response, "cartItems",
                    JSON.toJSONString(cartItems), 3600 * 3, true);

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
    @LoginRequire(loginSuccess = false)
    public String toCartList(HttpServletRequest request, ModelMap modelMap) {

        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String memberId = (String) request.getAttribute("memberId");
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

        modelMap.put("cartList", omsCartItems);
        modelMap.put("totalAmount", getTotalAmount(omsCartItems));
        return "cartList";
    }

    /**
     * 计算购物车总价格
     *
     * @param omsCartItems 购物车中所有商品
     * @return {@link BigDecimal} 总价格
     */
    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal total = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {
            if ("1".equals(omsCartItem.getIsCheck())) {
                total = omsCartItem.getTotalPrice().add(total);
            }
        }
        return total;
    }

    @PostMapping("checkCart")
    @LoginRequire
    public String checkCart(OmsCartItem omsCartItem, ModelMap modelMap, HttpServletRequest request) {
        String memberId = (String) request.getAttribute("memberId");
        omsCartItem.setMemberId(memberId);
        //修改更新数据库
        cartService.checkCart(omsCartItem);
        //从缓存中获取数据
        List<OmsCartItem> cartList = cartService.getCartList(omsCartItem.getMemberId());

        modelMap.put("totalAmount", getTotalAmount(cartList));
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
                break;
            }
        }
        return isExist;
    }
}
