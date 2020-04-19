package com.sen.gmal.api.service;

import com.sen.gmal.api.beans.UmsMemberReceiveAddress;
import java.util.List;

/**
 * @Author: Sen
 * @Date: 2019/10/27 16:41
 * @Description:
 */
public interface UmsMemberReciveAddressService {

    List<UmsMemberReceiveAddress> selectByUmsMemberId(String memberId);

    UmsMemberReceiveAddress getDeliveryAddressByAddressId(String addressId);
}
