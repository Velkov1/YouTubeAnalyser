package com.youtube.analyser.youtubenalyser.model.videoCategory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class VideoCategoryListResponse {
    private String nextPageToken;
    private List<VideoCategoryResponse> items;
}
