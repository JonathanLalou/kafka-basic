package com.github.jonathanlalou.kafkabasic.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document
@Builder
public class Book implements Serializable {
    @Id
    @JsonProperty("book")
    private int book;

    private int size;

    private List<Chapter> chapters = new ArrayList<>();

}
