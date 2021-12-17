package com.github.jonathanlalou.kafkabasic.dto;

import com.github.jonathanlalou.kafkabasic.domain.Book;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class JsonBookLoadingResult {
    Integer letterAbsoluteRank;
    Book book;
    List<Letter> letters;
}
