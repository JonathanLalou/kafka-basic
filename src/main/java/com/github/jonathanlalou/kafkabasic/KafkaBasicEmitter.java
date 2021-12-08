package com.github.jonathanlalou.kafkabasic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
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

    public String hello() throws Exception {
        logger.info("GET hello");
        final CountDownLatch countDownLatch = new CountDownLatch(messagePerRequest);
        for (int i = 0; i < messagePerRequest; i++) {
            logger.info("Sending #{}", i);
            this.template.send(
                    topicName
                    , String.valueOf(i)
                    , new Letter(
                            'A'
                            , 1, 1, 1, 1, 1, 1, i
                    )
            );
        }
        //noinspection ResultOfMethodCallIgnored
        countDownLatch.await(1, TimeUnit.SECONDS);
        logger.info("All messages were sent");
        return "hello world!";
    }

}
