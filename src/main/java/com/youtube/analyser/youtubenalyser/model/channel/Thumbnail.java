package com.youtube.analyser.youtubenalyser.model.channel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class Thumbnail {
    private String url;
    private int width;
    private int height;
}
