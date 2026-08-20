package com.youtube.analyser.youtubenalyser.model.video;

import com.youtube.analyser.youtubenalyser.model.channel.Thumbnail;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class VideoDTO {

    private String videoId;
    private Instant savedToRepo;
    private Instant publishedAt;
    private String channelId;
    private String videoTitle;
    private String videoDescription;
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
}
