package com.youtube.analyser.youtubenalyser.model.region;

import com.youtube.analyser.youtubenalyser.client.YouTubeClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RegionCodeCache {
    private final YouTubeClient youTubeClient;
    private Set<String> validRegionCodes = Set.of();

    @PostConstruct
    public void init() {
        I18nRegionListResponse response = youTubeClient.getI18nRegions();
        this.validRegionCodes = response.getItems().stream()
                .map(I18nRegionsResponse::getId)
                .collect(Collectors.toSet());
    }

    public boolean isValid(String regionCode) {
        return regionCode != null && validRegionCodes.contains(regionCode.toUpperCase());
    }

}

