package com.youtube.analyser.youtubeanalyser.model.channel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class YTChannelSnapshotDTO {
    private String channelId;
    private long totalViews;
    private long subscribers;
    private long numVideos;
    private Instant fetchedAt;

}
