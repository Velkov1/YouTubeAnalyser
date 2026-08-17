package com.youtube.analyser.youtubenalyser.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class VideoSnippet {
    private Instant publishedAt;
    private String channelId;
    private String videoTitle;
    private String videoDescription;
    private Map<String, Thumbnail> thumbnails;
    private String channelTitle;
    private List<String> tags;
    private String categoryId;
    private String liveBroadcastContent;
    private String defaultLanguage;

}
