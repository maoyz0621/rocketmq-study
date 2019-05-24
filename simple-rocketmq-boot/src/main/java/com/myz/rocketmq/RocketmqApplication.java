package com.myz.rocketmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RocketmqApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(RocketmqApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
