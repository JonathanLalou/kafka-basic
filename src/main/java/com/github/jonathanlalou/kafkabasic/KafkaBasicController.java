package com.github.jonathanlalou.kafkabasic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@RestController
public class KafkaBasicController {
    private static final Logger logger = LoggerFactory.getLogger(KafkaBasicController.class);

    private final KafkaTemplate<String, Object> template;
    private final String topicName;
    private final int messagePerRequest;

    public KafkaBasicController(
            final KafkaTemplate<String, Object> template
            , @Value("${tpd.topic-name}") final String topicName
            , @Value("${tpd.messages-per-request}") final int messagesPerRequest
    ) {
        this.template = template;
        this.topicName = topicName;
        this.messagePerRequest = messagesPerRequest;
    }

    @GetMapping("/hello")
    public String hello() throws Exception {
        logger.info("GET hello");
        final CountDownLatch countDownLatch = new CountDownLatch(messagePerRequest);
        for (int i = 0; i < messagePerRequest; i++) {
            this.template.send(
                    topicName
                    , String.valueOf(i)
                    , new PracticalAdvice(
                            "a Practical Advice"
                            , i
                    )
            );
        }
        //noinspection ResultOfMethodCallIgnored
        countDownLatch.await(30, TimeUnit.SECONDS);
        logger.info("All messages were sent");
        return "hello world!";
    }

}
