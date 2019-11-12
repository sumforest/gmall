package com.sen.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.sen.gmal.api.beans.UmsMember;
import com.sen.gmal.api.service.UmsMemberService;
import com.sen.gmall.user.mapper.UmsMemberMapper;
import com.sen.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/10/27 03:49
 * @Description:
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {

    @Autowired
    private UmsMemberMapper umsMemberMapper;


    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public List<UmsMember> listUmsMember() {
        return umsMemberMapper.selectAll();
    }

    @Override
    public UmsMember login(UmsMember member) {
        RLock lock = redissonClient.getLock("lock");
        try (Jedis jedis = redisUtil.getJedis()) {

            if (jedis != null) {
                //从缓存获取
                String memberString = jedis.get("user:" + member.getPassword() + ":info");
                if (StringUtils.isNotBlank(memberString)) {
                    return JSON.parseObject(memberString, UmsMember.class);
                }
                //从缓存中获取失败，开启数据库
                List<UmsMember> loginUmsMembers = umsMemberMapper.select(member);
                if (loginUmsMembers != null && loginUmsMembers.size() == 1) {
                    UmsMember loginUmsmember = loginUmsMembers.get(0);
                    //写入缓存
                    jedis.setex("user:" + member.getPassword() + ":info", 3600 * 24, JSON.toJSONString(loginUmsmember));
                    return loginUmsmember;
                }
                return null;
            }
            //缓存挂了，开数据库（加锁）
            lock.lock();
            List<UmsMember> loginUmsMembers = umsMemberMapper.select(member);
            if (loginUmsMembers != null && loginUmsMembers.size() == 1) {
                return loginUmsMembers.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    @Override
    public void addToCache(String id, String token) {
        try (Jedis jedis = redisUtil.getJedis()) {
            jedis.setex("user:" + id + ":token", 3600 * 2, token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public UmsMember checkAuthMember(String idstr) {
        UmsMember umsMember = new UmsMember();
        umsMember.setSourceUid(idstr);
        return umsMemberMapper.selectOne(umsMember);
    }

    @Override
    public String addMember(UmsMember umsMember) {
        umsMemberMapper.insert(umsMember);
        return umsMember.getId();
    }
}
