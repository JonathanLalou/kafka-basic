package com.github.jonathanlalou.kafkabasic.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * "ELS" stands for "Equidistant Letter Sequence"
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Document
@Builder
public class Els implements Serializable {
    @Id
    private String id;
    // TODO replace with a sequence-generated Integer

    /**
     * points to Letter.absoluteRank
     */
    private Integer firstLetter;

    private Integer interval;

    private String content;

//    public String getKey() {
//        return interval + "-" + firstLetter;
//    }
}
