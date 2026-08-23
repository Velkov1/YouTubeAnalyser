package com.youtube.analyser.youtubenalyser.model.video;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class YTVideoSnapshotDTO {
    private String videoId;
    private long viewCount;
    private long likeCount;
    private long commentCount;
    private Instant fetchedAt;
}