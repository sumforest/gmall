package com.sen.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sen.gmal.api.beans.UmsMemberReceiveAddress;
import com.sen.gmal.api.service.UmsMemberReciveAddressService;
import com.sen.gmall.user.mapper.UmsMemberReciveAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * @Author: Sen
 * @Date: 2019/10/27 16:41
 * @Description:
 */
@Service
public class UmsMemberReciveAddressServiceImpl implements UmsMemberReciveAddressService {

    @Autowired
    private UmsMemberReciveAddressMapper mapper;

    @Override
    public List<UmsMemberReceiveAddress> selectByUmsMemberId(String memberId) {
        UmsMemberReceiveAddress address = new UmsMemberReceiveAddress();
        address.setMemberId(memberId);
        return mapper.select(address);
    }

    @Override
    public UmsMemberReceiveAddress getDeliveryAddressByAddressId(String addressId) {
        UmsMemberReceiveAddress address = new UmsMemberReceiveAddress();
        address.setId(addressId);
        return mapper.selectOne(address);
    }

}
