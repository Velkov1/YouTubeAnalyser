package com.youtube.analyser.youtubenalyser.model.channel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class ChannelDTO {

    //last updated at:
    private Instant savedToTheRepo;

    private String ytId;
    private String channelName;
    private String description;
    private String channelUrl;
    private Instant creationDate;
    private String country;
    private String language;
    private List<Thumbnail> thumbnails;
    private String uploadedVidsPlaylistId;
    private long totalViews;
    private long subscribers;
    private long numVideos;
    private String privacyStatus;
    private boolean madeForKids;
    private double averageViewsPerVideo;
    private double viewsPerSubscriber;
    private long channelAgeInMonths;
    private double uploadsPerMonth;
}
