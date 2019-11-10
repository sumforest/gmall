package com.sen.gmall.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Auther: Sen
 * @Date: 2019/11/10 22:57
 * @Description:
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.sen.gmall")
public class GmallPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPaymentApplication.class, args);
    }
}
