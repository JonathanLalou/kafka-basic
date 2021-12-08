package com.github.jonathanlalou.kafkabasic;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Letter(
        @JsonProperty("character") Character character
        , @JsonProperty("book") int book
        , @JsonProperty("chapter") int chapter
        , @JsonProperty("verse") int verse
        , @JsonProperty("bookRank") int bookRank
        , @JsonProperty("chapterRank") int chapterRank
        , @JsonProperty("verseRank") int verseRank
        , @JsonProperty("absoluteRank") int absoluteRank // pk
) {
}
