package com.youtube.analyser.youtubeanalyser.model.comment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CommentThreadResponse {
    private String id;
    private Snippet snippet;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Snippet{
        private String channelId;
        private String videoId;
        private CommentResponse topLevelComment;
        private int totalReplyCount;
    }
}
