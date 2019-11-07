package com.sen.gmall.api.service;

import com.sen.gmall.api.beans.OmsCartItem;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/8 00:13
 * @Description:
 */
public interface OmsCartItemService{
    OmsCartItem getCartItemByMemberIdAndSkuId(String skuId, String memberId);

    void add(OmsCartItem omsCartItem);

    void update(OmsCartItem cartItemFormDb);

    void flushCache(String memberId);

    List<OmsCartItem> getCartList(String memberId);

    void checkCart(OmsCartItem omsCartItem);
}
