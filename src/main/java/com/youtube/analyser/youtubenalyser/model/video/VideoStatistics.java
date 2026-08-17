package com.youtube.analyser.youtubenalyser.model.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoStatistics {
    private long viewCount;
    private long likeCount;
    private long commentCount;

}
