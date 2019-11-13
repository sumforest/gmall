package com.sen.gmall.seckill.controller;

import com.sen.gmall.util.RedisUtil;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/13 02:12
 * @Description:
 */
@Controller
public class SeckillController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 先到先得式秒杀
     * @return
     */
    @GetMapping("/redissonkill")
    @ResponseBody
    public String redissonkill() {
        RSemaphore redissonkill = redissonClient.getSemaphore("mate30pro");
        boolean b = redissonkill.tryAcquire();
        if (b) {
            System.out.println("抢购成功");
        } else {
            System.out.println("抢购失败!!!");
        }
        return "1";
    }

    /**
     * 拼运气式秒杀
     * @return
     */
    @GetMapping("/kill")
    @ResponseBody
    public String seckill() {
        String memberId = "1";
        Jedis jedis = null;
        try {

            jedis = redisUtil.getJedis();
            jedis.watch("mate30pro");
            int stock = Integer.parseInt(jedis.get("mate30pro"));

            if (stock > 0) {
                Transaction multi = jedis.multi();
                multi.decrBy("mate30pro", 1);
                List<Object> exec = multi.exec();
                if (exec != null && exec.size() > 0) {
                    System.out.println("恭喜用户：" + memberId + "成功抢购华为mate30pro一台，剩余库存：" + (stock - 1));
                } else {
                    System.out.println("抢购失败，剩余库存：" + stock + "请重试");
                }
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return "1";
    }
}
