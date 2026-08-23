package com.youtube.analyser.youtubenalyser.controller;

import com.youtube.analyser.youtubenalyser.model.comment.YTCommentDTO;
import com.youtube.analyser.youtubenalyser.service.CommentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @GetMapping("/{videoInput}/all")
    public List<YTCommentDTO> getCommentsOfVideo(@PathVariable String videoInput){
        return commentService.getCommentsOfVideo(videoInput);
    }

    @GetMapping("{channelInput}/channel_comments")
    public List<YTCommentDTO> getAllCommentsOfChannel(@PathVariable String channelInput){
        return commentService.getAllCommentsOfChannel(channelInput);
    }


}
