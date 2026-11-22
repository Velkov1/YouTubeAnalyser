package com.youtube.analyser.youtubeanalyser.model.playlist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PlaylistItem {
    private Snippet snippet;


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Snippet{
        private ResourceId resourceId;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ResourceId{
        private String videoId;
    }
}
