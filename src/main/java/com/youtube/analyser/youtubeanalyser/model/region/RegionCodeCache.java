package com.youtube.analyser.youtubeanalyser.model.region;

import com.youtube.analyser.youtubeanalyser.client.YouTubeClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class RegionCodeCache {
    private final YouTubeClient youTubeClient;
    private Set<String> validRegionCodes = Set.of();

    @PostConstruct
    public void init() {
        refresh();
    }

    public boolean isValid(String regionCode) {
        if(validRegionCodes.isEmpty()){
            refresh();
        }
        return regionCode != null && validRegionCodes.contains(regionCode.toUpperCase());
    }

    private void refresh(){
        try {
            I18nRegionListResponse response = youTubeClient.getI18nRegions();
            validRegionCodes = response.getItems().stream()
                    .map(I18nRegionsResponse::getId)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.warn("Failed to load valid region codes from YouTube API, region validation will be temporarily unavailable: {}", e.getMessage());
        }
    }

}

