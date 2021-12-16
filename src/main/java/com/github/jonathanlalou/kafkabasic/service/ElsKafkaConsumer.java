package com.github.jonathanlalou.kafkabasic.service;

import com.github.jonathanlalou.kafkabasic.config.KafkaConsumerConfig;
import com.github.jonathanlalou.kafkabasic.domain.Els;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import com.github.jonathanlalou.kafkabasic.repository.ElsRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.stream.StreamSupport;

@Component
@ConditionalOnNotWebApplication
@Getter
@Setter
@Profile("!feed")
@Slf4j
public class ElsKafkaConsumer {
    @Value("${els.messages-per-request}")
    int messagesPerRequest;

    @Autowired
    private ElsRepository elsRepository;

    @KafkaListener(
            topics = "${els.topic-name}", clientIdPrefix = "json", containerFactory = KafkaConsumerConfig.KAFKA_LISTENER_OBJECT_CONTAINER_FACTORY
    )
    public void listenAsObject(ConsumerRecord<String, Els> consumerRecord, @Payload Els payload) {
        final CountDownLatch countDownLatch = new CountDownLatch(messagesPerRequest);
        if (0 == payload.getFirstLetter() % 10000) {
            log.info(
                    "Logger 1 [JSON] received key {}: Type [{}] | Payload: {} | Record {}"
                    , consumerRecord.key()
                    , typeIdHeader(consumerRecord.headers())
                    , payload
                    , consumerRecord
            );
        }
        elsRepository.save(payload);
        countDownLatch.countDown();
    }

    private static String typeIdHeader(Headers headers) {
        return StreamSupport.stream(headers.spliterator(), false)
                .filter(header -> header.key().equals("__TypeId__"))
                .findFirst().map(header -> new String(header.value())).orElse("N/A");
    }
}
