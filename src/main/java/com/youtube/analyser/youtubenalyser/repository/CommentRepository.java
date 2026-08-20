package com.youtube.analyser.youtubenalyser.repository;

import com.youtube.analyser.youtubenalyser.model.comment.YTComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<YTComment, String> {
}
