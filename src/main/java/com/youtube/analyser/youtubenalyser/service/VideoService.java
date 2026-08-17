package com.youtube.analyser.youtubenalyser.service;

import com.youtube.analyser.youtubenalyser.client.YouTubeClient;
import com.youtube.analyser.youtubenalyser.model.VideoDTO;
import com.youtube.analyser.youtubenalyser.model.VideoResponse;
import com.youtube.analyser.youtubenalyser.model.YTVideo;
import com.youtube.analyser.youtubenalyser.model.YTVideoSnapshot;
import com.youtube.analyser.youtubenalyser.repository.VideoRepository;
import com.youtube.analyser.youtubenalyser.repository.YTVideoSnapshotRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@AllArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final YTVideoSnapshotRepository snapshotRepository;
    private final YouTubeClient youTubeClient;
    private final String parts = "snippet, contentDetails, status, statistics, hasPaidProductPlacementDetails";

    //fetching data with video id
    public VideoDTO getVideoDataWithId(String videoId){
        return toYTVideoDTO(getOrCreateVideo(videoId));
    }
    private YTVideo getOrCreateVideo(String videoId){
        return videoRepository.findByVideoId(videoId)
                .filter(this::isNew)
                .orElseGet(() -> fetchAndSaveVideo(videoId));
    }

    private boolean isNew(YTVideo video){
        return video.getSavedToRepo() != null && video.getSavedToRepo().isAfter(Instant.now().minus(Duration.ofHours(24)));
    }

    private YTVideo fetchAndSaveVideo(String videoId){
        VideoResponse response = youTubeClient.getVideoDataWithVideoId(parts, videoId);
        YTVideo video = toYTVideo(response);
        snapshotRepository.save(new YTVideoSnapshot(video.getVideoId(), video.getViewCount(), video.getLikeCount(), video.getCommentCount()));
        return videoRepository.save(video);
    }
    private YTVideo toYTVideo(VideoResponse response){
        return new YTVideo(
                response.getId(),
                response.getSnippet().getPublishedAt(), response.getSnippet().getChannelId(), response.getSnippet().getVideoTitle(), response.getSnippet().getVideoDescription(), response.getSnippet().getThumbnails().values().stream().toList(),response.getSnippet().getChannelTitle(), response.getSnippet().getTags(), response.getSnippet().getCategoryId(), response.getSnippet().getLiveBroadcastContent(), response.getSnippet().getDefaultLanguage(),
                response.getContentDetails().getDuration(), response.getContentDetails().getDefinition(), response.getContentDetails().getCaption(), response.getContentDetails().getRegionRestriction().getAllowed(), response.getContentDetails().getRegionRestriction().getBlocked(),
                response.getStatus().getPrivacyStatus(),response.getStatus().isMadeForKids(),
                response.getStatistics().getViewCount(), response.getStatistics().getLikeCount(), response.getStatistics().getCommentCount(),
                response.getHasPaidProductPlacementDetails().isHasPaidProductPlacement()
        );
    }
    private VideoDTO toYTVideoDTO(YTVideo video){
        return new VideoDTO(
                video.getSavedToRepo(),
                video.getPublishedAt(), video.getChannelId(), video.getVideoTitle(), video.getVideoDescription(), video.getThumbnails(), video.getChannelTitle(), video.getTags(), video.getCategoryId(), video.getLiveBroadcastContent(), video.getDefaultLanguage(),
                video.getDuration(), video.getDefinition(), video.getCaption(),
                video.getAllowed(), video.getBlocked(),
                video.getPrivacyStatus(), video.isMadeForKids(),
                video.getViewCount(), video.getLikeCount(), video.getCommentCount(),
                video.isHasPaidProductPlacement()
        );
    }

    // fetch by multiple video ids


}
