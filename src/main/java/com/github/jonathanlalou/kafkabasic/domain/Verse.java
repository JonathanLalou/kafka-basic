package com.github.jonathanlalou.kafkabasic.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document
@Builder
public class Verse {
    @Id
//    @JsonProperty("verse")
    private int verse;

//    @JsonProperty("size")
    private int size;

    @JsonIgnore
    private String content;
}
