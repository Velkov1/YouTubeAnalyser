package com.youtube.analyser.youtubeanalyser.repository;

import com.youtube.analyser.youtubeanalyser.model.channel.YTChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelRepository extends JpaRepository<YTChannel, String>{
    Optional<YTChannel> findByChannelUrl(String handle);
}
