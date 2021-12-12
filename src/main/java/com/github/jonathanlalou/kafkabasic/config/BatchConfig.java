package com.github.jonathanlalou.kafkabasic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jonathanlalou.kafkabasic.domain.Book;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

@Configuration
@EnableBatchProcessing
//@ConditionalOnNotWebApplication
@Profile("feed")
public class BatchConfig {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value("${file.input}")
    private String fileInput;

    @Bean
    @StepScope
    public JsonItemReader<Book> bookJsonItemReader() {
//        LOGGER.info(LOG_TEMPLATE, getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "Inside Reader...");

        final ObjectMapper mapper = new ObjectMapper();

        final JacksonJsonObjectReader<Book> jsonObjectReader = new JacksonJsonObjectReader<>(Book.class);
        jsonObjectReader.setMapper(mapper);
        return new JsonItemReaderBuilder<Book>()
                .jsonObjectReader(jsonObjectReader)
                .resource(new ClassPathResource(fileInput))
                .name("bookJsonItemReader")
                .build();

    }

    @Bean
    public ItemWriter<Book> bookItemWriter() {
        return list -> {
            for (Book book : list) {
                System.out.println("Writer: " + book.toString().substring(0, 100));
            }
        };
    }

//    @Bean
//    public ItemProcessor<Book, Book> bookItemProcessor() {
//        return book -> {
//            System.out.println("In processor: " + book.toString().substring(0, 100));
//            System.out.println("book.getText().size(): " + book.getText().size());
//            final List<String> strings = book.getText().get(0);
//            for (int j = 0; j < 3; j++) {
//                final String chapter = strings.get(j);
////            for (String verse : strings) {
//                for (int i = 0; i < chapter.length(); i++) {
//                    System.out.println(chapter.charAt(i));
//                }
//            }
//            System.out.println("book.getText().size(): " + strings);
//            return book;
//        };
//    }

    @Bean
    public Job importBookJob(
//            JobCompletionNotificationListener listener,
            Step step1) {
        return jobBuilderFactory.get("importBookJob")
                .incrementer(new RunIdIncrementer())
//                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(
            ItemReader<Book> bookItemReader
            , ItemProcessor<Book, Book> bookItemProcessor
            , ItemWriter<Book> bookItemWriter
    ) {
        return stepBuilderFactory.get("step1")
                .<Book, Book>chunk(10)
                .reader(bookItemReader)
                .processor(bookItemProcessor)
                .writer(bookItemWriter)
                .build();
    }

}
