package com.youtube.analyser.youtubenalyser.model.channel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Statistics {
    private long viewCount;
    private long subscriberCount;
    private boolean hiddenSubscriberCount;
    private long videoCount;
}
