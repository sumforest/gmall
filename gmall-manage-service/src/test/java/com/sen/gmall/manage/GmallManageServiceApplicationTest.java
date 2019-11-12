package com.sen.gmall.manage;

import com.sen.gmall.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

/**
 * @Auther: Sen
 * @Date: 2019/11/4 18:51
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTest {

    @Autowired
    private RedisUtil redisUtil;
    @Test
    public void redisTest() {
        Jedis jedis = redisUtil.getJedis();
        System.out.println(jedis);
    }
}
