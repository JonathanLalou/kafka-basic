package com.github.jonathanlalou.kafkabasic.config;

import com.github.jonathanlalou.kafkabasic.batch.ElsGeneratorTasklet;
import com.github.jonathanlalou.kafkabasic.batch.ElsSendToKafkaTasklet;
import com.github.jonathanlalou.kafkabasic.batch.GrossDataSendToKafkaTasklet;
import com.github.jonathanlalou.kafkabasic.batch.JsonFileLoadTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
    public static final String JSON_FILE_LOAD_TASKLET_STEP = "jsonFileLoadTaskletStep";
    public static final String GROSS_DATA_SEND_TO_KAFKA_TASKLET_STEP = "grossDataSendToKafkaTaskletStep";
    public static final String ELS_GENERATOR_STEP = "elsGeneratorStep";
    public static final String ELS_SEND_TO_KAFKA_STEP = "ElsSendToKafkaStep";
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job(
            @Qualifier(JSON_FILE_LOAD_TASKLET_STEP) Step jsonFileLoadTaskletStep
            , @Qualifier(GROSS_DATA_SEND_TO_KAFKA_TASKLET_STEP) Step grossDataSendToKafkaTaskletStep
            , @Qualifier(ELS_GENERATOR_STEP) Step elsGeneratorStep
            , @Qualifier(ELS_SEND_TO_KAFKA_STEP) Step elsSendToKafkaStep
    ) {
        return jobBuilderFactory
                .get("taskletsJob")
                .start(jsonFileLoadTaskletStep)
                .next(grossDataSendToKafkaTaskletStep)
                .next(elsGeneratorStep)
                .next(elsSendToKafkaStep)
                .build();
    }

    @Bean(JSON_FILE_LOAD_TASKLET_STEP)
    protected Step jsonFileLoadTaskletStep(
            JsonFileLoadTasklet jsonFileLoadTasklet
    ) {
        return stepBuilderFactory
                .get(JSON_FILE_LOAD_TASKLET_STEP)
                .tasklet(jsonFileLoadTasklet)
                .build();
    }

    @Bean(GROSS_DATA_SEND_TO_KAFKA_TASKLET_STEP)
    protected Step grossDataSendToKafkaTaskletStep(
            GrossDataSendToKafkaTasklet grossDataSendToKafkaTasklet
    ) {
        return stepBuilderFactory
                .get(GROSS_DATA_SEND_TO_KAFKA_TASKLET_STEP)
                .tasklet(grossDataSendToKafkaTasklet)
                .build();
    }

    @Bean(ELS_GENERATOR_STEP)
    protected Step equidistantLetterSequenceGeneratorStep(
            ElsGeneratorTasklet elsGeneratorTasklet
    ) {
        return stepBuilderFactory
                .get(ELS_GENERATOR_STEP)
                .tasklet(elsGeneratorTasklet)
                .build();
    }

    @Bean(ELS_SEND_TO_KAFKA_STEP)
    protected Step equidistantLetterSequenceSendToKafkaStep(
            ElsSendToKafkaTasklet elsSendToKafkaTasklet
    ) {
        return stepBuilderFactory
                .get(ELS_SEND_TO_KAFKA_STEP)
                .tasklet(elsSendToKafkaTasklet)
                .build();
    }
}
