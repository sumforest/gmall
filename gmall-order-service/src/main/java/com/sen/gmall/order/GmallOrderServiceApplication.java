package com.sen.gmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author: Sen
 * @Date: 2019/11/10 00:13
 * @Description:
 */
@SpringBootApplication
@MapperScan("com.sen.gmall.order.mapper")
@ComponentScan(basePackages = "com.sen.gmall")
public class GmallOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallOrderServiceApplication.class, args);
    }
}
