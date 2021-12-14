package com.github.jonathanlalou.kafkabasic.config;

import com.github.jonathanlalou.kafkabasic.batch.EquidistantLetterSequenceGeneratorTasklet;
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
    public static final String JSON_FILE_LOAD_TASKLET_STEP = "jsonFileLoadTaskletStep";
    public static final String GROSS_DATA_SEND_TO_KAFKA_TASKLET_STEP = "grossDataSendToKafkaTaskletStep";
    public static final String EQUIDISTANT_LETTER_SEQUENCE_GENERATOR_STEP = "equidistantLetterSequenceGeneratorStep";
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
            @Qualifier(JSON_FILE_LOAD_TASKLET_STEP) Step jsonFileLoadTaskletStep
            , @Qualifier(GROSS_DATA_SEND_TO_KAFKA_TASKLET_STEP) Step grossDataSendToKafkaTaskletStep
            , @Qualifier(EQUIDISTANT_LETTER_SEQUENCE_GENERATOR_STEP) Step equidistantLetterSequences
    ) {
        return jobBuilderFactory
                .get("taskletsJob")
                .start(jsonFileLoadTaskletStep)
                .next(grossDataSendToKafkaTaskletStep)
                .next(equidistantLetterSequences)
                // TODO 1/ create the list of equidistant letter strings
                //      2/ send them to Kafka
                .build();
    }

    @Bean(JSON_FILE_LOAD_TASKLET_STEP) // TODO extract constants
    protected Step jsonFileLoadTaskletStep(
            JsonFileLoadTasklet jsonFileLoadTasklet
    ) {
        return stepBuilderFactory
                .get(JSON_FILE_LOAD_TASKLET_STEP)
                .tasklet(jsonFileLoadTasklet)
                .build();
    }

    @Bean(GROSS_DATA_SEND_TO_KAFKA_TASKLET_STEP) // TODO extract constants
    protected Step grossDataSendToKafkaTaskletStep(
            GrossDataSendToKafkaTasklet grossDataSendToKafkaTasklet
    ) {
        return stepBuilderFactory
                .get(GROSS_DATA_SEND_TO_KAFKA_TASKLET_STEP)
                .tasklet(grossDataSendToKafkaTasklet)
                .build();
    }

    @Bean(EQUIDISTANT_LETTER_SEQUENCE_GENERATOR_STEP) // TODO extract constants
    protected Step equidistantLetterSequenceGeneratorStep(
            EquidistantLetterSequenceGeneratorTasklet equidistantLetterSequenceGeneratorTasklet
    ) {
        return stepBuilderFactory
                .get(EQUIDISTANT_LETTER_SEQUENCE_GENERATOR_STEP)
                .tasklet(equidistantLetterSequenceGeneratorTasklet)
                .build();
    }
}
