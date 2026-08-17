package com.youtube.analyser.youtubenalyser.model.channel;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Entity
public class ChannelUploadsCache {
    @Id
    private String channelId;

    @ElementCollection(fetch= FetchType.EAGER)
    @CollectionTable(name="playlist_cache_video_ids",
                     joinColumns = @JoinColumn(name="channel_id"))
    @Column(name="video_id")
    private List<String> videoIds;

    private Instant lastFetchedAt;

    public ChannelUploadsCache(String channelId, List<String> videoIds) {
        this.channelId = channelId;
        this.videoIds = videoIds;
        this.lastFetchedAt = Instant.now();
    }
}
