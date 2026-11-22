package com.youtube.analyser.youtubeanalyser.repository;

import com.youtube.analyser.youtubeanalyser.model.video.VideoDTO;
import com.youtube.analyser.youtubeanalyser.model.video.YTVideo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<YTVideo, String> {
    Optional<YTVideo> findByVideoId(String videoId);
    List<YTVideo> findByChannelIdOrderByPublishedAtDesc(String channelId, Pageable pageable);
    List<YTVideo> findByChannelIdOrderByViewCountDesc(String channelId, Pageable pageable);

    List<YTVideo> findByChannelIdOrderByPublishedAtAsc(String channelId, Pageable pageable);
    List<YTVideo> findByChannelIdOrderByViewCountAsc(String channelId, Pageable pageable);

    List<VideoDTO> findByChannelIdAndPublishedAtBetween(String channelId, Instant startDate, Instant endDate);

    List<VideoDTO> findByChannelIdAndPublishedAtBefore(String channelId, Instant date);

    List<VideoDTO> findByChannelIdAndPublishedAtAfter(String channelId, Instant date);
}
