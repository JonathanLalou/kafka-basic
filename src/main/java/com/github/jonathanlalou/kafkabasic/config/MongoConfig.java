package com.github.jonathanlalou.kafkabasic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(
        basePackages = {
                "com.github.jonathanlalou.kafkabasic"
//                , "com.github.jonathanlalou.kafkabasic.repository"
        })
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Override
    protected String getDatabaseName() {
        return "myFirstDatabase";
//        return "ghardaia";
    }
}
