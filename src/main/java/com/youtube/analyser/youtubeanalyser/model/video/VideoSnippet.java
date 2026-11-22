package com.youtube.analyser.youtubeanalyser.model.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.youtube.analyser.youtubeanalyser.model.channel.Thumbnail;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class VideoSnippet {
    private Instant publishedAt;
    private String channelId;
    @JsonProperty("title")
    private String videoTitle;
    @JsonProperty("description")
    private String videoDescription;
    private Map<String, Thumbnail> thumbnails;
    private String channelTitle;
    private List<String> tags;
    private String categoryId;
    private String liveBroadcastContent;
    private String defaultLanguage;

}
