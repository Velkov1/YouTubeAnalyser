package com.youtube.analyser.youtubenalyser.repository;

import com.youtube.analyser.youtubenalyser.model.YTVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<YTVideo, String> {
    Optional<YTVideo> findByVideoId(String videoId);
}
