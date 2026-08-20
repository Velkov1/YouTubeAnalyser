package com.youtube.analyser.youtubenalyser.model.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class YTCommentDTO {
    private String videoId;
    private String authorName;
    private String text;
    private long likeCount;
    private Instant publishedAt;
}
