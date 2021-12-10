package com.github.jonathanlalou.kafkabasic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

// TODO remove the exclude and find a better solution in the YML file
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableAsync
public class KafkaBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaBasicApplication.class, args);
    }
}
