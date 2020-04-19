package com.sen.gmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author: Sen
 * @Date: 2019/11/7 17:51
 * @Description:
 */
@SpringBootApplication
@MapperScan("com.sen.gmall.cart.mapper")
@ComponentScan("com.sen.gmall")
public class GmallCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallCartServiceApplication.class, args);
    }
}
