package com.github.jonathanlalou.kafkabasic.domain;

import lombok.Data;

import java.util.List;

@Data
public class Book {
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
