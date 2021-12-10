package com.github.jonathanlalou.kafkabasic.config;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@ConditionalOnNotWebApplication
@Profile("consumer")
public class KafkaConsumerConfig {
    public static final String KAFKA_LISTENER_OBJECT_CONTAINER_FACTORY = "kafkaListenerObjectContainerFactory";

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    public Deserializer<String> stringDeserializer() {
        return new StringDeserializer();
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(Deserializer<String> stringDeserializer) {
        final JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(), stringDeserializer, jsonDeserializer);
    }

    @Bean(name = KAFKA_LISTENER_OBJECT_CONTAINER_FACTORY)
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerObjectContainerFactory(ConsumerFactory<String, Object> consumerFactory) {
        final ConcurrentKafkaListenerContainerFactory<String, Object> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory);
        return concurrentKafkaListenerContainerFactory;
    }

    @Bean
    public ConsumerFactory<String, String> stringConsumerFactory(Deserializer<String> stringDeserializer) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(), stringDeserializer, stringDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerStringContainerFactory(ConsumerFactory<String, String> stringStringConsumerFactory) {
        final ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(stringStringConsumerFactory);
        return concurrentKafkaListenerContainerFactory;
    }

    @Bean
    public ConsumerFactory<String, byte[]> byteArrayConsumerFactory(Deserializer<String> stringDeserializer) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(), stringDeserializer, new ByteArrayDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaListenerByteArrayContainerFactory(ConsumerFactory<String, byte[]> byteArrayConsumerFactory) {
        final ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(byteArrayConsumerFactory);
        return factory;
    }
}
