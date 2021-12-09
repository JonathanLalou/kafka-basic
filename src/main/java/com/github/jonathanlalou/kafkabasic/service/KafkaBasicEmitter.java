package com.github.jonathanlalou.kafkabasic.service;

import com.github.jonathanlalou.kafkabasic.domain.Letter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaBasicEmitter {
    private static final Logger logger = LoggerFactory.getLogger(KafkaBasicEmitter.class);

    private final KafkaTemplate<String, Object> template;
    private final String topicName;
    private final int messagePerRequest;

    public KafkaBasicEmitter(
            final KafkaTemplate<String, Object> template
            , @Value("${tpd.topic-name}") final String topicName
            , @Value("${tpd.messages-per-request}") final int messagesPerRequest
    ) {
        this.template = template;
        this.topicName = topicName;
        this.messagePerRequest = messagesPerRequest;
    }

    @Async
    public Future<String> sendMessagesToKafka() throws Exception {
        logger.info("Starting `sendMessagesToKafka`");
        final CountDownLatch countDownLatch = new CountDownLatch(messagePerRequest);
        for (int i = 0; i < messagePerRequest; i++) {
            final int ii = i;
            CompletableFuture.supplyAsync(() -> {
                try {
                    return sendOneMessage(countDownLatch, ii);
                } catch (InterruptedException e) {
                    logger.error("Could not send message for: " + ii, e);
                    return null;
                }
            });
        }

        countDownLatch.await(1, TimeUnit.SECONDS);
        logger.info("All messages were sent");
        return new AsyncResult<>("sendMessagesToKafka world!");
    }

    @Async
    protected Future<Boolean> sendOneMessage(CountDownLatch countDownLatch, int i) throws InterruptedException {
        logger.info("Sending #{}", i);
        this.template.send(
                topicName
                , String.valueOf(i)
                , new Letter(
                        'A'
                        , 1, 1, 1, 1, 1, 1, i
                )
        );
        countDownLatch.await(5, TimeUnit.SECONDS);
        return new AsyncResult<>(true);
    }

}
