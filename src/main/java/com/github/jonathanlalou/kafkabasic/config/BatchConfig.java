package com.github.jonathanlalou.kafkabasic.config;

import com.github.jonathanlalou.kafkabasic.batch.GrossDataSendToKafkaTasklet;
import com.github.jonathanlalou.kafkabasic.batch.JsonFileLoadTasklet;
import com.github.jonathanlalou.kafkabasic.domain.Book;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableBatchProcessing
//@ConditionalOnNotWebApplication
@Profile("feed")
public class BatchConfig {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public ItemWriter<Book> bookItemWriter() {
        return list -> {
            for (Book book : list) {
                System.out.println("Writer: " + book.toString().substring(0, 100));
            }
        };
    }

    @Bean
    public Job job(
            @Qualifier("jsonFileLoadTaskletStep") Step jsonFileLoadTaskletStep
            , @Qualifier("grossDataSendToKafkaTaskletStep") Step grossDataSendToKafkaTaskletStep
    ) {
        return jobBuilderFactory
                .get("taskletsJob")
                .start(jsonFileLoadTaskletStep)
                .next(grossDataSendToKafkaTaskletStep)
                // TODO 1/ create the list of equidistant letter strings
                //      2/ send them to Kafka
                .build();
    }

    @Bean("jsonFileLoadTaskletStep") // TODO extract constants
    protected Step jsonFileLoadTaskletStep(
            JsonFileLoadTasklet jsonFileLoadTasklet
    ) {
        return stepBuilderFactory
                .get("jsonFileLoadTasklet")
                .tasklet(jsonFileLoadTasklet)
                .build();
    }

    @Bean("grossDataSendToKafkaTaskletStep") // TODO extract constants
    protected Step grossDataSendToKafkaTaskletStep(
            GrossDataSendToKafkaTasklet grossDataSendToKafkaTasklet
    ) {
        return stepBuilderFactory
                .get("grossDataSendToKafkaTaskletStep")
                .tasklet(grossDataSendToKafkaTasklet)
                .build();
    }
}
