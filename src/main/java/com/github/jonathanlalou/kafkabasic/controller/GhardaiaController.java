package com.github.jonathanlalou.kafkabasic.controller;

import com.github.jonathanlalou.kafkabasic.dto.WordSearchResult;
import com.github.jonathanlalou.kafkabasic.service.GhardaiaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Slf4j
public class GhardaiaController {
    public static final String DEFAULT_RANGE = "20";
    public static final String DEFAULT_MAX = "3";
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
    public List<WordSearchResult> search(
            @PathVariable("word") String word
            , @RequestParam(name = "max", required = false, defaultValue = DEFAULT_MAX) Integer max
    ) throws Exception {
        log.info("GET /search/{}", word);
        return ghardaiaService.search(word, max, Integer.parseInt(DEFAULT_RANGE));
    }

    @GetMapping(value = "/find/{word}")
    public String find(
            @PathVariable("word") String word
            , @RequestParam(name = "max", required = false, defaultValue = DEFAULT_MAX) Integer max
            , @RequestParam(name = "range", required = false, defaultValue = DEFAULT_RANGE) Integer range
            , Model model
    ) throws Exception {
        log.info("GET /find/{}", word);
        final List<WordSearchResult> wordSearchResults = ghardaiaService.search(word, max, range);
        model.addAttribute("max", max);
        model.addAttribute("range", range);
        model.addAttribute("wordSearchResults", wordSearchResults);
        return "found";
    }


}
