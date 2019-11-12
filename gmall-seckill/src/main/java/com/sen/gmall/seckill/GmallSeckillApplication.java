package com.sen.gmall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Auther: Sen
 * @Date: 2019/11/13 02:10
 * @Description:
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.sen.gmall")
public class GmallSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallSeckillApplication.class, args);
    }
}
