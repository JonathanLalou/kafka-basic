package com.github.jonathanlalou.kafkabasic.config;

import com.github.jonathanlalou.kafkabasic.batch.ElsGeneratorTasklet;
import com.github.jonathanlalou.kafkabasic.batch.GhardaiaCleaningTasklet;
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
            , @Qualifier(ELS_GENERATOR_STEP) Step elsGeneratorStep
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
                .next(elsGeneratorStep)
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


    @Bean(ELS_GENERATOR_STEP)
    protected Step equidistantLetterSequenceGeneratorStep(
            ElsGeneratorTasklet elsGeneratorTasklet
    ) {
        return stepBuilderFactory
                .get(ELS_GENERATOR_STEP)
                .tasklet(elsGeneratorTasklet)
                .build();
    }


}
