package com.youtube.analyser.youtubenalyser.repository;

import com.youtube.analyser.youtubenalyser.model.YTVideoSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YTVideoSnapshotRepository extends JpaRepository<YTVideoSnapshot, Long> {
}
