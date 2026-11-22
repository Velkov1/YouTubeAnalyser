package com.youtube.analyser.youtubeanalyser.repository;

import com.youtube.analyser.youtubeanalyser.model.channel.ChannelUploadsCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelUploadsCacheRepository extends JpaRepository<ChannelUploadsCache, String> {
    List<String> findByChannelId(String channelId);
}
