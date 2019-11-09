package com.sen.gmall.api.service;

import com.sen.gmall.api.beans.UmsMember;
import com.sen.gmall.api.beans.UmsMemberReceiveAddress;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/10/27 03:48
 * @Description:
 */
public interface UmsMemberService {

    List<UmsMember> listUmsMember();

    UmsMember login(UmsMember member);

    void addToCache(String id, String token);

    UmsMember checkAuthMember(String idstr);

    void addMember(UmsMember umsMember);

}
