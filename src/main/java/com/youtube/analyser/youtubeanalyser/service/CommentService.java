package com.youtube.analyser.youtubeanalyser.service;

import com.youtube.analyser.youtubeanalyser.client.YouTubeClient;
import com.youtube.analyser.youtubeanalyser.model.comment.*;
import com.youtube.analyser.youtubeanalyser.model.video.VideoDTO;
import com.youtube.analyser.youtubeanalyser.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class CommentService {
    private static final int MAX_COMMENTS_PER_VIDEO = 500;

    private final YouTubeClient youTubeClient;
    private final CommentRepository commentRepository;
    private final VideoService videoService;
    private final ChannelService channelService;

    public List<YTCommentDTO> getCommentsOfVideo(String videoInput){
        String videoId = videoService.toVideoId(videoInput);
        List<YTCommentDTO> result = new ArrayList<>();
        String nextPageToken = null;
        do{
            String parts = "id, snippet";
            CommentThreadListResponse response = youTubeClient.getCommentThreadsByVideo(parts, videoId, nextPageToken);
            for(CommentThreadResponse thread : response.getItems()){
                YTComment saved = commentRepository.save(toYTComment(thread));
                result.add(toYTCommentDTO(saved));
            }
            nextPageToken = response.getNextPageToken();
        }while(nextPageToken != null && result.size() < MAX_COMMENTS_PER_VIDEO);
        return result;
    }

    private YTComment toYTComment(CommentThreadResponse thread){
        CommentResponse topLevel = thread.getSnippet().getTopLevelComment();
        CommentResponse.Snippet snippet = topLevel.getSnippet();
        return new YTComment(
                topLevel.getId(),
                thread.getSnippet().getVideoId(),
                topLevel.getSnippet().getAuthorDisplayName(),
                topLevel.getSnippet().getTextDisplay(),
                topLevel.getSnippet().getLikeCount(),
                topLevel.getSnippet().getPublishedAt());
    }

    private YTCommentDTO toYTCommentDTO(YTComment comment){
        return new YTCommentDTO(
                comment.getVideoId(),
                comment.getAuthorName(),
                comment.getText(),
                comment.getLikeCount(),
                comment.getPublishedAt()
        );
    }

    public List<YTCommentDTO> getAllCommentsOfChannel(String channelInput){
        String channelId = channelService.toChannelId(channelInput);
        List<VideoDTO> videos = videoService.getChannelVideos(channelId);
        List<YTCommentDTO> allComments = new ArrayList<>();
        for(VideoDTO video : videos){
            allComments.addAll(getCommentsOfVideo(video.getVideoId()));
        }
        return allComments;
    }

}
