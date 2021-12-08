package com.github.jonathanlalou.kafkabasic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaBasicController {
    private static final Logger logger = LoggerFactory.getLogger(KafkaBasicController.class);

    @Autowired
    private KafkaBasicEmitter kafkaBasicEmitter;

    @GetMapping("/hello")
    public String hello() throws Exception {
        logger.info("GET hello");
        return kafkaBasicEmitter.hello();
    }

}
