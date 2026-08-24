package com.youtube.analyser.youtubenalyser.model.channel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "youtube channel")
@Getter
@NoArgsConstructor
public class YTChannel {

    @Id
    private String ytId;
    @Setter
    private Instant savedToTheRepo;
    private String channelName;
    private String description;
    private String channelUrl;
    private Instant creationDate;
    private String country;
    private String language;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "channel_thumbnails", joinColumns = @JoinColumn(name = "channel_id"))
    private List<Thumbnail> thumbnails;
    private String uploadedVidsPlaylistId;
    private long totalViews;
    private long subscribers;
    private long numVideos;
    private String privacyStatus;
    private boolean madeForKids;

    public YTChannel(String ytId, String channelName, String description, String channelUrl, Instant creationDate, String country, String language, List<Thumbnail> thumbnails, String uploadedVidsPlaylistId, long totalViews, long subscribers, long numVideos, String privacyStatus, boolean madeForKids) {
        this.ytId = ytId;
        this.channelName = channelName;
        this.description = description;
        this.channelUrl = channelUrl;
        this.creationDate = creationDate;
        this.country = country;
        this.language = language;
        this.thumbnails = thumbnails;
        this.uploadedVidsPlaylistId = uploadedVidsPlaylistId;
        this.totalViews = totalViews;
        this.subscribers = subscribers;
        this.numVideos = numVideos;
        this.privacyStatus = privacyStatus;
        this.madeForKids = madeForKids;
        this.savedToTheRepo = Instant.now();
    }
}
