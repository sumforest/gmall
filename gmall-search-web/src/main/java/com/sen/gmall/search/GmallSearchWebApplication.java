package com.sen.gmall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: Sen
 * @Date: 2019/11/6 01:13
 * @Description:
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.sen.gmall")
public class GmallSearchWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallSearchWebApplication.class, args);
    }
}
