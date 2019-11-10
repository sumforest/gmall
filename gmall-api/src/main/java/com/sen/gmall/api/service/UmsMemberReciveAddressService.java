package com.sen.gmall.api.service;

import com.sen.gmall.api.beans.UmsMemberReceiveAddress;
import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/10/27 16:41
 * @Description:
 */
public interface UmsMemberReciveAddressService {

    List<UmsMemberReceiveAddress> selectByUmsMemberId(String memberId);

    UmsMemberReceiveAddress getDeliveryAddressByAddressId(String addressId);
}
