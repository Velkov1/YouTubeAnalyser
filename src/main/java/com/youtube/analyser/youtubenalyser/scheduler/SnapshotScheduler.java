package com.youtube.analyser.youtubenalyser.scheduler;

import com.youtube.analyser.youtubenalyser.model.channel.YTChannel;
import com.youtube.analyser.youtubenalyser.model.video.YTVideo;
import com.youtube.analyser.youtubenalyser.repository.ChannelRepository;
import com.youtube.analyser.youtubenalyser.repository.VideoRepository;
import com.youtube.analyser.youtubenalyser.service.ChannelService;
import com.youtube.analyser.youtubenalyser.service.VideoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class SnapshotScheduler {
    private final VideoService videoService;
    private final VideoRepository videoRepository;
    private final ChannelService channelService;
    private final ChannelRepository channelRepository;

    @Scheduled(fixedDelayString = "${snapshot.refresh-interval-ms}", initialDelayString = "${snapshot.refresh-interval-ms}")
    public void refreshAllVideoSnapshots(){
        List<String> videoIds = videoRepository.findAll().stream().map(YTVideo::getVideoId).toList();
        log.info("Starting scheduled snapshot refresh for {} videos", videoIds.size());
        for(String videoId : videoIds) {
            try {
                videoService.refreshSnapshot(videoId);
            } catch (Exception e) {
                log.warn("Failed to refresh snapshot for video {}: {}", videoId, e.getMessage());
            }
        }
    }

    @Scheduled(fixedDelayString = "${snapshot.refresh-interval-ms}", initialDelayString = "${snapshot.refresh-interval-ms}")
    public void refreshAllChannelSnapshots(){
        List<String> channelIds = channelRepository.findAll().stream().map(YTChannel::getYtId).toList();
        log.info("Starting scheduled snapshot refresh for {} channels", channelIds.size());
        for(String channelId : channelIds){
            try{
                channelService.refreshSnapshot(channelId);
            } catch (Exception e){
                log.warn("Failed to refresh snapshot for channel {}: {}", channelId, e.getMessage());
            }
        }
    }
}
