package com.youtube.analyser.youtubenalyser.model.comment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "youtube_comment")
@Getter
@NoArgsConstructor
public class YTComment {
    @Id
    private String commentId;
    private String videoId;
    private String authorName;
    private String text;
    private long likeCount;
    private Instant publishedAt;
    private Instant savedToRepo;

    public YTComment(String commentId, String videoId, String authorName, String text, long likeCount, Instant publishedAt) {
        this.commentId = commentId;
        this.videoId = videoId;
        this.authorName = authorName;
        this.text = text;
        this.likeCount = likeCount;
        this.publishedAt = publishedAt;
        this.savedToRepo = Instant.now();
    }
}
