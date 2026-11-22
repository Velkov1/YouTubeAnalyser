package com.youtube.analyser.youtubeanalyser.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SearchListResponse {
    private List<SearchResponse> items;
}
