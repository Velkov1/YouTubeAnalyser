package com.youtube.analyser.youtubenalyser.repository;

import com.youtube.analyser.youtubenalyser.model.video.VideoDTO;
import com.youtube.analyser.youtubenalyser.model.video.YTVideoSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface YTVideoSnapshotRepository extends JpaRepository<YTVideoSnapshot, Long> {
    List<YTVideoSnapshot> findFirst2ByVideoIdOrderByFetchedAtDesc(String videoId);

    Optional<YTVideoSnapshot> findFirstByVideoIdOrderByFetchedAtAsc(String videoId);

    Optional<YTVideoSnapshot> findFirstByVideoIdOrderByFetchedAtDesc(String videoId);

    List<YTVideoSnapshot> findFirst4ByVideoIdOrderByFetchedAtDesc(String videoId);

    List<YTVideoSnapshot> findByVideoIdOrderByFetchedAtDesc(String videoId);
}
