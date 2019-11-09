package com.sen.gmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Auther: Sen
 * @Date: 2019/11/2 00:15
 * @Description:
 */
@SpringBootApplication
@MapperScan(basePackages = "com.sen.gmall.user.mapper")
@ComponentScan(basePackages = "com.sen.gmall")
public class GmallUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallUserServiceApplication.class, args);
    }
}
