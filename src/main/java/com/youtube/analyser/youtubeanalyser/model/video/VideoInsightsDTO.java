package com.youtube.analyser.youtubeanalyser.model.video;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoInsightsDTO {
    private VideoDTO video;
    private long viewIncreaseFromLastSnapshot;
    private double averageViewsVelocity;
    private double averageCommentVelocity;
    private double averageLikesVelocity;
    private double momentum;
    private boolean outperformingChannel;
}