package com.youtube.analyser.youtubeanalyser.repository;

import com.youtube.analyser.youtubeanalyser.model.comment.YTComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<YTComment, String> {
}
