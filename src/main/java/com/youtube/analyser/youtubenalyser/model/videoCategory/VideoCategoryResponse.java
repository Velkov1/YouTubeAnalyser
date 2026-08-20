package com.youtube.analyser.youtubenalyser.model.videoCategory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class VideoCategoryResponse {
    private String id;
    private Snippet snippet;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Snippet{
        private String title;
    }
}


