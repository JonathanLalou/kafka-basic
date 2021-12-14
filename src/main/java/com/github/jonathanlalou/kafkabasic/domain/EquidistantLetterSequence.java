package com.github.jonathanlalou.kafkabasic.domain;

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
public class EquidistantLetterSequence implements Serializable {
    @Id
    private Integer id;

    /**
     * points to Letter.absoluteRank
     */
    private Integer firstLetter;

    private Integer interval;

    private String content;
}
