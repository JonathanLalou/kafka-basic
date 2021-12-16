package com.github.jonathanlalou.kafkabasic.config;

import com.github.jonathanlalou.kafkabasic.batch.ElsGeneratorTasklet;
import com.github.jonathanlalou.kafkabasic.batch.ElsSendToKafkaTasklet;
import com.github.jonathanlalou.kafkabasic.batch.GhardaiaCleaningTasklet;
import com.github.jonathanlalou.kafkabasic.batch.LetterSendToKafkaTasklet;
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
    public static final String LETTER_SEND_TO_KAFKA_STEP = "letterSendToKafkaStep";
    public static final String ELS_GENERATOR_STEP = "elsGeneratorStep";
    public static final String ELS_SEND_TO_KAFKA_STEP = "ElsSendToKafkaStep";
    public static final String GHARDAIA_CLEANING_STEP = "ghardaiaCleaningStep";
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job(
            @Qualifier(JSON_FILE_LOAD_TASKLET_STEP) Step jsonFileLoadTaskletStep
            , @Qualifier(LETTER_SEND_TO_KAFKA_STEP) Step letterSendToKafkaStep
            , @Qualifier(ELS_GENERATOR_STEP) Step elsGeneratorStep
            , @Qualifier(ELS_SEND_TO_KAFKA_STEP) Step elsSendToKafkaStep
//            , @Qualifier(GHARDAIA_CLEANING_STEP) Step ghardaiaCleaningStep
            , GhardaiaCleaningTasklet ghardaiaCleaningTasklet
    ) {
        return jobBuilderFactory
                .get("taskletsJob")
                .start(stepBuilderFactory
                        .get(GHARDAIA_CLEANING_STEP)
                        .tasklet(ghardaiaCleaningTasklet)
                        .build()
                )
                .next(jsonFileLoadTaskletStep)
                .next(letterSendToKafkaStep)
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

    @Bean(LETTER_SEND_TO_KAFKA_STEP)
    protected Step grossDataSendToKafkaTaskletStep(
            LetterSendToKafkaTasklet letterSendToKafkaTasklet
    ) {
        return stepBuilderFactory
                .get(LETTER_SEND_TO_KAFKA_STEP)
                .tasklet(letterSendToKafkaTasklet)
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

//    @Bean(GHARDAIA_CLEANING_STEP)
//    protected Step ghardaiaCleaningStep(
//            GhardaiaCleaningTasklet ghardaiaCleaningTasklet
//    ) {
//        return stepBuilderFactory
//                .get(GHARDAIA_CLEANING_STEP)
//                .tasklet(ghardaiaCleaningTasklet)
//                .build();
//    }
}
