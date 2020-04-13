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

    /**
     * 令牌存储到缓存
     * @param id 当前登录用户id
     * @param token 令牌
     */
    void addToCache(String id, String token);

    /**
     * 查询该用户是否存在数据库中
     * @param idstr 微博id
     * @return {@link UmsMember} 不存在返回null
     */
    UmsMember checkAuthMember(String idstr);

    String addMember(UmsMember umsMember);

}
