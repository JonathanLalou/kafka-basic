package com.github.jonathanlalou.kafkabasic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class KafkaBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaBasicApplication.class, args);
    }
}
