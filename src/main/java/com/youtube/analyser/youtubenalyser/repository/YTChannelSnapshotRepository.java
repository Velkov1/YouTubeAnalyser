package com.youtube.analyser.youtubenalyser.repository;

import com.youtube.analyser.youtubenalyser.model.channel.YTChannelSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YTChannelSnapshotRepository extends JpaRepository<YTChannelSnapshot, Long> {
    List<YTChannelSnapshot> findByChannelIdOrderByFetchedAtDesc(String channelId);
}
