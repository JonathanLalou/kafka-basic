package com.github.jonathanlalou.kafkabasic.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document
@Builder
public class BookDTO {
    private String status;
    private String versionTitle;
    private List<String> sectionNames;
    private String license;
    private String language;
    private String title;
    private Boolean licenseVetted;
    private List<List<String>> text;
    private String versionSource;
    private String heTitle;
    private String versionTitleInHebrew;
    private List<String> categories;
}
