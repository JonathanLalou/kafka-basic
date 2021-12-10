package com.github.jonathanlalou.kafkabasic.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document
public class Letter {
    @Id
    @JsonProperty("absoluteRank")
    private int absoluteRank;

    @JsonProperty("character")
    private Character character;
    @JsonProperty("book")
    private int book;
    @JsonProperty("chapter")
    private int chapter;
    @JsonProperty("verse")
    private int verse;
    @JsonProperty("bookRank")
    private int bookRank;
    @JsonProperty("chapterRank")
    private int chapterRank;
    @JsonProperty("verseRank")
    private int verseRank;


}
