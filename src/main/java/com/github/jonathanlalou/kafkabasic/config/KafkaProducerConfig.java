package com.github.jonathanlalou.kafkabasic.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

@Lazy
@Configuration
@Profile("!web")
public class KafkaProducerConfig {
    @Autowired
    private KafkaProperties kafkaProperties;

    @Value("${tpd.topic-name}")
    private String topicName;

    @Value("${letters.topic-name}")
    private String lettersTopicName;

    @Value("${els.topic-name}")
    private String elsTopicName;

    @Bean
    public Executor asyncExecutor(
            @Value("${spring.task.execution.pool.core-size}") final Integer corePoolSize
            , @Value("${spring.task.execution.pool.core-size}") final Integer maxPoolSize
            , @Value("${spring.task.execution.pool.queue-capacity}") final Integer queueCapacity
    ) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("Async-");
        /*
        Fixes error:
        ```
        org.springframework.core.task.TaskRejectedException: Executor [java.util.concurrent.ThreadPoolExecutor@d701f70[Running, pool size = 8, active threads = 8, queued tasks = 100, completed tasks = 0]] did not accept task: org.springframework.aop.interceptor.AsyncExecutionInterceptor$$Lambda$1098/0x00000008010f0ad8@b86abe5
        ```
        Source: https://stackoverflow.com/questions/49290054/
        */
        executor.setRejectedExecutionHandler((runnable, threadPoolExecutor) -> {
            try {
                threadPoolExecutor.getQueue().put(runnable);
            } catch (InterruptedException e) {
                throw new RejectedExecutionException("There was an unexpected InterruptedException whilst waiting to add a Runnable in the executor's blocking queue", e);
            }
        });

        executor.initialize();
        return executor;
    }

    // Producer configuration
    @Bean
    public Map<String, Object> producerConfigs() {
        final Map<String, Object> producerConfigs = new HashMap<>(kafkaProperties.buildProducerProperties());
        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        /*
        Fixes error:
        ```
        org.apache.kafka.common.errors.RecordTooLargeException: The message is 1203697 bytes when serialized which is larger than 1048576, which is the value of the max.request.size configuration.
        ```
        By default, the value is 1MB ; the full Tanach is 1_203_486 characters. Let's increase the limit + take in account the serialization.
         * */
        producerConfigs.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 1_210_000);
        return producerConfigs;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() { // TODO move autowired object to method definition
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic adviceTopic() {
        return new NewTopic(topicName, 3, (short) 1);
    }

    @Bean
    public NewTopic lettersTopic() {
        return new NewTopic(lettersTopicName, 3, (short) 1);
    }

    @Bean
    public NewTopic elsTopic() {
        return new NewTopic(elsTopicName, 3, (short) 1);
    }

}
