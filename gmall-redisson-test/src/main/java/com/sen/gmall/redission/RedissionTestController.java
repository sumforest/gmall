package com.sen.gmall.redission;

import com.sen.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

/**
 * @Auther: Sen
 * @Date: 2019/11/5 01:14
 * @Description:
 */
@Controller
public class RedissionTestController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/redissontest")
    @ResponseBody
    public String redissonTest() {

        Jedis jedis = redisUtil.getJedis();
        RLock lock = redissonClient.getLock("lock");

        //上锁
        lock.lock();
        try {
            String value = jedis.get("key");

            if (StringUtils.isBlank(value)) {
                value = "1";
            }
            System.out.println("->" + value);
            jedis.set("key", (Integer.parseInt(value) + 1) + "");

        } finally {
            jedis.close();
            //释放锁
            lock.unlock();

        }
        return "success";
    }

}
