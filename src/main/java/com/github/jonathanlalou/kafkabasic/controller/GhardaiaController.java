package com.github.jonathanlalou.kafkabasic.controller;

import com.github.jonathanlalou.kafkabasic.dto.WordSearchResult;
import com.github.jonathanlalou.kafkabasic.repository.ElsRepository;
import com.github.jonathanlalou.kafkabasic.repository.LetterRepository;
import com.github.jonathanlalou.kafkabasic.service.GhardaiaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class GhardaiaController {
    @Autowired
    private GhardaiaService ghardaiaService;

    @GetMapping("/hello")
    @ResponseBody
    public String hello() throws Exception {
        log.info("GET /hello");
        return "hello world!";
    }

    @GetMapping(value = "/search/{word}")
    @ResponseBody()
    public List<WordSearchResult> letter(
            @PathVariable("word") String word
            , @RequestParam(name="max", required=false, defaultValue="3") Integer max
    ) throws Exception {
        log.info("GET /search/{}", word);
        return ghardaiaService.search(word, max);
    }


}
