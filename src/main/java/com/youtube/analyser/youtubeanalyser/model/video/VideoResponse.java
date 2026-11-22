package com.youtube.analyser.youtubeanalyser.model.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.youtube.analyser.youtubeanalyser.model.channel.Status;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoResponse {
    private String etag;
    private String id;
    private VideoSnippet snippet;
    private VideoContentDetails contentDetails;
    private Status status;
    private VideoStatistics statistics;
    private VideoPaidProductPlacementDetails paidProductPlacementDetails;

}
