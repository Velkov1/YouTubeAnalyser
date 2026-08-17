package com.youtube.analyser.youtubenalyser.model.channel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@NoArgsConstructor
@Data
//historical snapshot of the channel
public class YTChannelSnapshot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String channelId;
    private long totalViews;
    private long subscribers;
    private long numVideos;
    private Instant fetchedAt;

    public YTChannelSnapshot(String channelId, long totalViews, long subscribers, long numVideos) {
        this.channelId = channelId;
        this.totalViews = totalViews;
        this.subscribers = subscribers;
        this.numVideos = numVideos;
        this.fetchedAt = Instant.now();
    }
}
