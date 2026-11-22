package com.youtube.analyser.youtubeanalyser.repository;

import com.youtube.analyser.youtubeanalyser.model.channel.YTChannelSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YTChannelSnapshotRepository extends JpaRepository<YTChannelSnapshot, Long> {
    List<YTChannelSnapshot> findByChannelIdOrderByFetchedAtDesc(String channelId);
}
