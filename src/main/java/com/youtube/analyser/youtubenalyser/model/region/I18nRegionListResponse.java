package com.youtube.analyser.youtubenalyser.model.region;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class I18nRegionListResponse {
    private List<I18nRegionsResponse> items;
}
