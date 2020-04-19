package com.sen.gmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: Sen
 * @Date: 2019/11/10 00:14
 * @Description:
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.sen.gmall")
public class GmallOrderWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallOrderWebApplication.class, args);
    }
}
