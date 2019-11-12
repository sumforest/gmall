package com.sen.gmal.api.service;

import com.sen.gmal.api.beans.UmsMember;

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

    String addMember(UmsMember umsMember);

}
