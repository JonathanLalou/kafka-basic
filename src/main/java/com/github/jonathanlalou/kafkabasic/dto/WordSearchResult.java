package com.github.jonathanlalou.kafkabasic.dto;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

@Data
@Builder
public class WordSearchResult {
    Integer interval;
    Integer firstLetter;
    Integer firstLetterRank;
    List<Triple<String, Character, String>> enclosings;
}
