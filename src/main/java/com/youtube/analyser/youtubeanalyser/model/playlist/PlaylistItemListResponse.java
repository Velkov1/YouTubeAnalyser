package com.youtube.analyser.youtubeanalyser.model.playlist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PlaylistItemListResponse {
    private String nextPageToken;
    private List<PlaylistItem> items;
}
