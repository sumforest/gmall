package com.sen.gmall.seckill.controller;

import com.sen.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

/**
 * @Auther: Sen
 * @Date: 2019/11/13 02:12
 * @Description:
 */
@Controller
public class SeckillController {

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/kill")
    @ResponseBody
    public String seckill() {
        String memberId = "1";
        Jedis jedis = redisUtil.getJedis();

        int stock = Integer.parseInt(jedis.get("mate30pro"));
        if (stock > 0) {
            jedis.decrBy("mate30pro", 1);
            System.out.println("恭喜用户：" + memberId + "成功抢购华为mate30pro一台，剩余库存：" + (stock - 1));
        }
        return "1";
    }
}
