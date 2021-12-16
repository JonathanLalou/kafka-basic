package com.github.jonathanlalou.kafkabasic.service;

import com.github.jonathanlalou.kafkabasic.domain.Els;
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
public class ElsKafkaProducer {

    private final KafkaTemplate<String, Object> template;
    private final String topicName;
    private final int messagePerRequest;

    public ElsKafkaProducer(
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
        log.warn("Starting `sendLettersToKafka`");

        for (Els els : elses) {
            sendOneElsToKafka(els);
        }

//        countDownLatch.await(1, TimeUnit.SECONDS);
        log.info("All messages were sent");
        return new AsyncResult<>("sendLettersToKafka world!");
    }

    @Async
    public void sendOneElsToKafka(Els els) {
        final CountDownLatch countDownLatch = new CountDownLatch(messagePerRequest);
        CompletableFuture.supplyAsync(() -> {
            try {
//                sendOneEls(els, countDownLatch);
                return sendOneEls(els, countDownLatch);
            } catch (InterruptedException e) {
                log.error("Could not send message for: #{}", els.getId(), e);
                return null;
            }
        });
    }

    @Async
    protected Future<Boolean> sendOneEls(Els els, CountDownLatch countDownLatch) throws InterruptedException {
//        if (els.getFirstLetter() % 100 == 0) {
//            System.gc();
//        }
        if (els.getFirstLetter() % 10000 == 0) {
            log.warn("Sending ELS with id: {}", els.getId());
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
