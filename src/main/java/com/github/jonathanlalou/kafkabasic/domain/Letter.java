package com.github.jonathanlalou.kafkabasic.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document
@Builder
public class Letter implements Serializable {
    @Id
//    @JsonProperty("absoluteRank")
    private int absoluteRank;

    //    @JsonProperty("character")
    private Character character;
    private Character heCharacter;
    //    @JsonProperty("book")
    private int book;
    //    @JsonProperty("chapter")
    private int chapter;
    //    @JsonProperty("verse")
    private int verse;
    //    @JsonProperty("bookRank")
    private int bookRank;
    //    @JsonProperty("chapterRank")
    private int chapterRank;
    //    @JsonProperty("verseRank")
    private int verseRank;
    //    @JsonProperty("finalLetter")
    private Boolean finalLetter;


}
