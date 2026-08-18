package com.youtube.analyser.youtubenalyser.model.region;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.youtube.analyser.youtubenalyser.model.channel.Snippet;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class I18nRegionsResponse {
    private String id;
    private Snippet snippet;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Snippet{
        private String gl;
        private String name;
    }
}
