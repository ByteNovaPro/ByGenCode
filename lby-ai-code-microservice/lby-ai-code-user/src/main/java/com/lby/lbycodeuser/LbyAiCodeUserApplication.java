package com.lby.lbycodeuser;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDubbo
@MapperScan("com.lby.lbycodeuser.mapper")
@ComponentScan("com.lby")
public class LbyAiCodeUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(LbyAiCodeUserApplication.class, args);
    }
}