package com.youtube.analyser.youtubenalyser.model.playlist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PlaylistItemListResponse {
    private String nextPageToken;
    private List<PlaylistItem> items;
}
