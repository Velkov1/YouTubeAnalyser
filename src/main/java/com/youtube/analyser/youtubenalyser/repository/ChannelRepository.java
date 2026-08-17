package com.youtube.analyser.youtubenalyser.repository;

import com.youtube.analyser.youtubenalyser.model.channel.YTChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelRepository extends JpaRepository<YTChannel, String>{
    Optional<YTChannel> findByChannelUrl(String handle);
}
