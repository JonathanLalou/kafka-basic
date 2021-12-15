package com.github.jonathanlalou.kafkabasic.controller;

import com.github.jonathanlalou.kafkabasic.domain.Els;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import com.github.jonathanlalou.kafkabasic.repository.ElsRepository;
import com.github.jonathanlalou.kafkabasic.repository.LetterRepository;
import com.github.jonathanlalou.kafkabasic.service.LetterKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class KafkaBasicController {
//    @Autowired
//    private LetterKafkaProducer letterKafkaProducer;

    @Autowired
    private LetterRepository letterRepository;
    @Autowired
    private ElsRepository elsRepository;

    @GetMapping("/hello")
    @ResponseBody
    public String hello() throws Exception {
        log.info("GET /hello");
        return "hello world!";
    }

//    @GetMapping("/letter")
//    @ResponseBody
//    public Letter letter() throws Exception {
//        log.info("GET /letter");
//        return letterRepository.save(new Letter(999, 'X', 'X', 1, 2, 3, 4, 5, 6, 7, false));
//    }

    @GetMapping("/search/{word}")
    @ResponseBody
    public List<Els> letter(@RequestParam String word) throws Exception {
        log.info("GET /search/{}", word);
        // TODO search for the reversed word
        return elsRepository.findByContentContains(word);
    }


}
