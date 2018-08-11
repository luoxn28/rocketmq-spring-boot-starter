package com.luo.boot.rocketmq.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author xiangnan
 * date 2018/8/3 16:54
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.luo.boot.rocketmq"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
