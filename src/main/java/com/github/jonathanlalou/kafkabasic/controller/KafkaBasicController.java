package com.github.jonathanlalou.kafkabasic.controller;

import com.github.jonathanlalou.kafkabasic.domain.Letter;
import com.github.jonathanlalou.kafkabasic.repository.LetterRepository;
import com.github.jonathanlalou.kafkabasic.service.KafkaBasicEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaBasicController {
    private static final Logger logger = LoggerFactory.getLogger(KafkaBasicController.class);

    @Autowired
    private KafkaBasicEmitter kafkaBasicEmitter;

    @GetMapping("/hello")
    public String hello() throws Exception {
        logger.info("GET /hello");
        return kafkaBasicEmitter.sendMessagesToKafka().get();
    }

    // DELETEME
    @Autowired
    private LetterRepository letterRepository;
    @GetMapping("/letter")
    @ResponseBody
    public Letter letter() throws Exception {
        logger.info("GET /letter");
        return letterRepository.save(new Letter(999, 'X','X', 1, 2, 3, 4, 5, 6, false));
    }


}
