package com.github.jonathanlalou.kafkabasic.service;

import com.github.jonathanlalou.kafkabasic.domain.Letter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class KafkaBasicEmitter {
//    private static final Logger logger = LoggerFactory.getLogger(KafkaBasicEmitter.class);

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
    public Future<String> sendMessagesToKafka(List<Letter> letters) throws Exception {
        log.warn("Starting `sendMessagesToKafka`");
        final CountDownLatch countDownLatch = new CountDownLatch(messagePerRequest);
        for (Letter letter : letters) {
            CompletableFuture.supplyAsync(() -> {
                try {
                    return sendOneMessage(letter, countDownLatch);
                } catch (InterruptedException e) {
                    log.error("Could not send message for: #{}: {} / {}", letter.getAbsoluteRank(), letter.getHeCharacter(), letter.getCharacter(), e);
                    return null;
                }
            });
        }

        countDownLatch.await(1, TimeUnit.SECONDS);
        log.info("All messages were sent");
        return new AsyncResult<>("sendMessagesToKafka world!");
    }

    @Async
    protected Future<Boolean> sendOneMessage(Letter letter, CountDownLatch countDownLatch) throws InterruptedException {
        if (letter.getAbsoluteRank() % 1000 == 0)
            log.warn("Sending #{}: {} / {}", letter.getAbsoluteRank(), letter.getHeCharacter(), letter.getCharacter());
        this.template.send(
                topicName
                , String.valueOf(letter.getAbsoluteRank())
                , letter
        );
        countDownLatch.await(5, TimeUnit.MILLISECONDS);
        return new AsyncResult<>(true);
    }

}
