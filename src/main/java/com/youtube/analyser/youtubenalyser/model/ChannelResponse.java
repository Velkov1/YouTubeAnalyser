package com.youtube.analyser.youtubenalyser.model;

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
