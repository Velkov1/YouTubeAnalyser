package com.youtube.analyser.youtubenalyser.model;

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
public class YTVideoSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String videoId;
    private long viewCount;
    private long likeCount;
    private long commentCount;
    private Instant fetchedAt;

    public YTVideoSnapshot(String videoId, long viewCount, long likeCount, long commentCount) {
        this.videoId = videoId;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.fetchedAt = Instant.now();
    }
}
