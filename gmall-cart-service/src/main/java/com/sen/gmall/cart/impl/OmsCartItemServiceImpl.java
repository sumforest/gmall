package com.sen.gmall.cart.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.sen.gmal.api.beans.OmsCartItem;
import com.sen.gmal.api.service.OmsCartItemService;
import com.sen.gmall.cart.mapper.OmscartItemMapper;
import com.sen.gmall.util.RedisUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Auther: Sen
 * @Date: 2019/11/8 00:15
 * @Description:
 */
@Service
public class OmsCartItemServiceImpl implements OmsCartItemService {

    @Autowired
    private OmscartItemMapper cartItemMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public OmsCartItem getCartItemByMemberIdAndSkuId(String skuId, String memberId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setMemberId(memberId);
        return cartItemMapper.selectOne(omsCartItem);
    }

    @Override
    public void add(OmsCartItem omsCartItem) {
        cartItemMapper.insertSelective(omsCartItem);
    }

    @Override
    public void update(OmsCartItem cartItemFormDb) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("id", cartItemFormDb.getId());
        cartItemMapper.updateByExampleSelective(cartItemFormDb, example);
    }

    @Override
    public void flushCache(String memberId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        List<OmsCartItem> omsCartItems = cartItemMapper.select(omsCartItem);


        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            Map<String, String> map = new HashMap<>();

            if (omsCartItems != null && omsCartItems.size() > 0) {
                for (OmsCartItem cartItem : omsCartItems) {
                    //设置小计价格
                    cartItem.setTotalPrice(cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
                    map.put(cartItem.getProductSkuId(), JSON.toJSONString(cartItem));
                }
            }
            jedis.del("user:" + memberId + ":cart");
            jedis.hmset("user:" + memberId + ":cart", map);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }


    }

    @Override
    public List<OmsCartItem> getCartList(String memberId) {
        List<OmsCartItem> cartItems = new ArrayList<>();
        Jedis jedis = null;

        RLock cartLock = redissonClient.getLock("CartLock");
        //上分布式锁，防止恶意缓存穿透
        cartLock.lock();
        try {
            jedis = redisUtil.getJedis();
            List<String> hvals = jedis.hvals("user:" + memberId + ":cart");
            for (String hval : hvals) {
                OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                cartItems.add(omsCartItem);
            }

            //缓存中没有数据从数据库中查询
            if (hvals.size() == 0) {
                OmsCartItem omsCartItem = new OmsCartItem();
                omsCartItem.setMemberId(memberId);
                cartItems = cartItemMapper.select(omsCartItem);

                //数据库中存在数据，放入缓存
                if (cartItems.size() > 0) {
                    Map<String, String> map = new HashMap<>();

                    for (OmsCartItem cartItem : cartItems) {
                        map.put(cartItem.getProductSkuId(), JSON.toJSONString(cartItem));
                    }
                    jedis.hmset("user:" + memberId + ":cart", map);
                } else {
                    jedis.setex("user:" + memberId + ":cart", 300, "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            cartLock.unlock();
        }

        //排序处理
        cartItems.sort(new Comparator<OmsCartItem>() {
            @Override
            public int compare(OmsCartItem o1, OmsCartItem o2) {
                return Integer.parseInt(o1.getProductSkuId()) - Integer.parseInt(o2.getProductSkuId());
            }

        });
       return cartItems;
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId", omsCartItem.getMemberId()).
                andEqualTo("productSkuId", omsCartItem.getProductSkuId());
        cartItemMapper.updateByExampleSelective(omsCartItem, example);

        //刷新缓存
        flushCache(omsCartItem.getMemberId());
    }
}
