package com.github.jonathanlalou.kafkabasic.service;

import com.github.jonathanlalou.kafkabasic.domain.Els;
import lombok.extern.slf4j.Slf4j;
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
public class ElsKakfaProducer {

    private final KafkaTemplate<String, Object> template;
    private final String topicName;
    private final int messagePerRequest;

    public ElsKakfaProducer(
            final KafkaTemplate<String, Object> template
            , @Value("${els.topic-name}") final String topicName
            , @Value("${els.messages-per-request}") final int messagesPerRequest
    ) {
        this.template = template;
        this.topicName = topicName;
        this.messagePerRequest = messagesPerRequest;
    }

    @Async
    public Future<String> sendElsesToKafka(List<Els> elses) throws Exception {
        log.warn("Starting `sendMessagesToKafka`");
        final CountDownLatch countDownLatch = new CountDownLatch(messagePerRequest);
        for (Els els : elses) {
            CompletableFuture.supplyAsync(() -> {
                try {
                    return sendOneEls(els, countDownLatch);
                } catch (InterruptedException e) {
                    log.error("Could not send message for: #{}", els.getId(), e);
                    return null;
                }
            });
        }

        countDownLatch.await(1, TimeUnit.SECONDS);
        log.info("All messages were sent");
        return new AsyncResult<>("sendMessagesToKafka world!");
    }

    @Async
    protected Future<Boolean> sendOneEls(Els els, CountDownLatch countDownLatch) throws InterruptedException {
        if (els.getFirstLetter() % 10000 == 0) {
            log.warn("Sending #{}", els.getId());
        }
        this.template.send(
                topicName
                , els.getId()
                , els
        );
        countDownLatch.await(5, TimeUnit.MILLISECONDS);
        return new AsyncResult<>(true);
    }

}
