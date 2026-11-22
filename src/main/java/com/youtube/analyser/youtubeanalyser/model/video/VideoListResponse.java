package com.youtube.analyser.youtubeanalyser.model.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class VideoListResponse {
    private List<VideoResponse> items;
}
