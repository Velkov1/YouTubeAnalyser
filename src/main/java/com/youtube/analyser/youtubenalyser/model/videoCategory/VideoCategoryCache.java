package com.youtube.analyser.youtubenalyser.model.videoCategory;

import com.youtube.analyser.youtubenalyser.client.YouTubeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VideoCategoryCache {
    private final YouTubeClient youTubeClient;
    private final Map<String, Set<String>> categoriesByRegion = new ConcurrentHashMap<>();

    public boolean isValid(String regionCode, String categoryId) {
        Set<String> categories = categoriesByRegion.computeIfAbsent(regionCode.toUpperCase(), this::fetchCategoryIds);
        return categories.contains(categoryId);
    }

    private Set<String> fetchCategoryIds(String regionCode){
        VideoCategoryListResponse response = youTubeClient.getVideoCategories(regionCode);
        return response.getItems().stream()
                .map(VideoCategoryResponse :: getId)
                .collect(Collectors.toUnmodifiableSet());
    }
}
