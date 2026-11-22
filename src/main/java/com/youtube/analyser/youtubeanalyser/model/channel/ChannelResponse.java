package com.youtube.analyser.youtubeanalyser.model.channel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelResponse {

    private String etag;
    private String id;
    private Snippet snippet;
    private ContentDetails contentDetails;
    private Statistics statistics;
    private Status status;

}
