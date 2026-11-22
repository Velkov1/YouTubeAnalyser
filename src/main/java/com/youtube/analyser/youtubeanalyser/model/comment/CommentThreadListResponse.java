package com.youtube.analyser.youtubeanalyser.model.comment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentThreadListResponse {
    private String nextPageToken;
    private List<CommentThreadResponse> items;
}
