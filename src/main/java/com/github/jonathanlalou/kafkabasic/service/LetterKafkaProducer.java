package com.github.jonathanlalou.kafkabasic.service;

import com.github.jonathanlalou.kafkabasic.domain.Letter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Lazy
@Slf4j
@Service
public class LetterKafkaProducer {

    private final KafkaTemplate<String, Object> template;
    private final String topicName;
    private final int messagePerRequest;

    public LetterKafkaProducer(
            final KafkaTemplate<String, Object> template
            , @Value("${letters.topic-name}") final String topicName
            , @Value("${letters.messages-per-request}") final int messagesPerRequest
    ) {
        this.template = template;
        this.topicName = topicName;
        this.messagePerRequest = messagesPerRequest;
    }

    @Async
    public Future<String> sendLettersToKafka(List<Letter> letters) throws Exception {
        log.warn("Starting `sendLettersToKafka`");
        for (Letter letter : letters) {
            sendOneLetterToKafka(letter);
        }

//        countDownLatch.await(1, TimeUnit.SECONDS);
        log.info("All messages were sent");
        return new AsyncResult<>("sendLettersToKafka world!");
    }

    @Async
    public void sendOneLetterToKafka(Letter letter) {
        final CountDownLatch countDownLatch = new CountDownLatch(messagePerRequest);
        CompletableFuture.supplyAsync(() -> {
            try {
                return sendOneMessage(letter, countDownLatch);
            } catch (InterruptedException e) {
                log.error("Could not send message for: #{}: {} / {}", letter.getAbsoluteRank(), letter.getHeCharacter(), letter.getCharacter(), e);
                return null;
            }
        });
        try {
            sendOneMessage(letter, countDownLatch);
        } catch (InterruptedException e) {
            log.error("Could not send message for: #{}: {} / {}", letter.getAbsoluteRank(), letter.getHeCharacter(), letter.getCharacter(), e);
        }

    }

    @Async
    protected Future<Boolean> sendOneMessage(Letter letter, CountDownLatch countDownLatch) throws InterruptedException {
        if (letter.getAbsoluteRank() % 10000 == 0) {
            log.info("Sending #{}: {} / {}", letter.getAbsoluteRank(), letter.getHeCharacter(), letter.getCharacter());
        }
        this.template.send(
                topicName
                , String.valueOf(letter.getAbsoluteRank())
                , letter
        );
        countDownLatch.await(5, TimeUnit.MILLISECONDS);
        return new AsyncResult<>(true);
    }

}
