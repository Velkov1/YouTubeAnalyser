package com.youtube.analyser.youtubenalyser.model.channel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class Snippet {
    private String title;
    private String description;
    private String customUrl;
    private Instant publishedAt;
    private Map<String, Thumbnail> thumbnails;
    private String defaultLanguage;
    private String country;
}
