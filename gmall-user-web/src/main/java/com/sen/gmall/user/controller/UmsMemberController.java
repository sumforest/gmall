package com.sen.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sen.gmal.api.beans.UmsMember;
import com.sen.gmal.api.beans.UmsMemberReceiveAddress;
import com.sen.gmal.api.service.UmsMemberReciveAddressService;
import com.sen.gmal.api.service.UmsMemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/10/27 03:49
 * @Description:
 */
@Controller
public class UmsMemberController {

    @Reference
    private UmsMemberService service;

    @Reference
    private UmsMemberReciveAddressService addressService;

    @GetMapping(value = "")
    @ResponseBody
    public String index() {
        return "hello gmall";
    }

    @GetMapping("list/members")
    @ResponseBody
    public List<UmsMember> listUmsMembers() {
        return service.listUmsMember();
    }

    @GetMapping("list/address")
    @ResponseBody
    public List<UmsMemberReceiveAddress> listUmsMemberAddress(String memberId) {
        return addressService.selectByUmsMemberId(memberId);
    }
}
