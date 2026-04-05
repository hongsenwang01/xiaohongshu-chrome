package com.example.hello;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class HelloApplication {

    /**
     * 设置应用时区为中国时区（Asia/Shanghai）
     */
    @PostConstruct
    public void init() {
        // 设置JVM默认时区为中国时区
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }

}
