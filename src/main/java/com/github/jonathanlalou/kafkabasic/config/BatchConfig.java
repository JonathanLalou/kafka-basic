package com.github.jonathanlalou.kafkabasic.config;

import com.github.jonathanlalou.kafkabasic.batch.ElsGeneratorTasklet;
import com.github.jonathanlalou.kafkabasic.batch.GhardaiaCleaningTasklet;
import com.github.jonathanlalou.kafkabasic.batch.JsonFileLoadTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public static final String GHARDAIA_CLEANING_STEP = "ghardaiaCleaningStep";
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job(
            JsonFileLoadTasklet jsonFileLoadTasklet
            , ElsGeneratorTasklet elsGeneratorTasklet
            , GhardaiaCleaningTasklet ghardaiaCleaningTasklet
    ) {
        return jobBuilderFactory
                .get("taskletsJob")
                .start(stepBuilderFactory
                        .get(GHARDAIA_CLEANING_STEP)
                        .tasklet(ghardaiaCleaningTasklet)
                        .build()
                )
                .next(stepBuilderFactory
                        .get(JSON_FILE_LOAD_TASKLET_STEP)
                        .tasklet(jsonFileLoadTasklet)
                        .build()
                )
                .next(stepBuilderFactory
                        .get(ELS_GENERATOR_STEP)
                        .tasklet(elsGeneratorTasklet)
                        .build())
                .build();
    }
}
