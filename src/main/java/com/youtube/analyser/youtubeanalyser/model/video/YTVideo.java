package com.youtube.analyser.youtubeanalyser.model.video;

import com.youtube.analyser.youtubeanalyser.model.channel.Thumbnail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "youtube video")
@Getter
@NoArgsConstructor
public class YTVideo {
    @Id
    private String videoId;
    @Setter
    private Instant savedToRepo;
    private Instant publishedAt;
    private String channelId;
    private String videoTitle;
    @Lob
    private String videoDescription;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "video_thumbnails", joinColumns = @JoinColumn(name = "video_id"))
    private List<Thumbnail> thumbnails;
    private String channelTitle;
    private List<String> tags;
    private String categoryId;
    private String liveBroadcastContent;
    private String defaultLanguage;
    private String duration;
    private String definition;
    private String caption;
    private List<String> allowed;
    private List<String> blocked;
    private String privacyStatus;
    private boolean madeForKids;
    private long viewCount;
    private long likeCount;
    private long commentCount;
    private boolean hasPaidProductPlacement;

    public YTVideo(String videoId, Instant publishedAt, String channelId, String videoTitle, String videoDescription, List<Thumbnail> thumbnails, String channelTitle, List<String> tags, String categoryId, String liveBroadcastContent, String defaultLanguage, String duration, String definition, String caption, List<String> allowed, List<String> blocked, String privacyStatus, boolean madeForKids, long viewCount, long likeCount, long commentCount, boolean hasPaidProductPlacement) {

        this.videoId = videoId;
        this.publishedAt = publishedAt;
        this.channelId = channelId;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.thumbnails = thumbnails;
        this.channelTitle = channelTitle;
        this.tags = tags;
        this.categoryId = categoryId;
        this.liveBroadcastContent = liveBroadcastContent;
        this.defaultLanguage = defaultLanguage;
        this.duration = duration;
        this.definition = definition;
        this.caption = caption;
        this.allowed = allowed;
        this.blocked = blocked;
        this.privacyStatus = privacyStatus;
        this.madeForKids = madeForKids;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.hasPaidProductPlacement = hasPaidProductPlacement;
        this.savedToRepo = Instant.now();
    }
}
