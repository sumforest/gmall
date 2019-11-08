package com.sen.gmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Auther: Sen
 * @Date: 2019/11/7 17:52
 * @Description:
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.sen.gmall")
public class GmallCartWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallCartWebApplication.class, args);
    }
}
