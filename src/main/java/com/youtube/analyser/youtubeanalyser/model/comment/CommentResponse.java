package com.youtube.analyser.youtubeanalyser.model.comment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CommentResponse {
    private String id;
    private Snippet snippet;
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Snippet{
        private String authorDisplayName;
        private String textDisplay;
        private long likeCount;
        private Instant publishedAt;
    }
}
