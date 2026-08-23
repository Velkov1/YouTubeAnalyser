package com.youtube.analyser.youtubenalyser.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SearchResponse {
    private Id id;
    private Snippet snippet;


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Id{
        private String videoId;
        private String channelId;
        private String playlistId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snippet {
        private Instant publishedAt;
        private String channelId;
        private String title;
        private String description;
        private String channelTitle;
    }
}
