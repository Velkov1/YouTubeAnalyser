package com.youtube.analyser.youtubenalyser.controller;

import com.youtube.analyser.youtubenalyser.model.video.VideoDTO;
import com.youtube.analyser.youtubenalyser.service.VideoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService){
        this.videoService = videoService;
    }

    @GetMapping("/{videoInput}")
    public VideoDTO getVideoData(@PathVariable String videoInput){
        return videoService.getVideoDataWithId(videoInput);
    }

    @GetMapping
    public List<VideoDTO> getManyVideos(@RequestParam List<String> videoInputs){
        return videoService.getManyVideos(videoInputs);
    }

    @GetMapping("/{channelInput}/all")
    public List<VideoDTO> getChannelVideos(@PathVariable String channelInput){
        return videoService.getChannelVideos(channelInput);
    }

    @GetMapping("/{channelInput}/most_watched/limit={limit}")
    public List<VideoDTO> topByViews(@PathVariable String channelInput,
                                     @PathVariable int limit){
        return videoService.topByViews(channelInput, limit);
    }

    @GetMapping("/{channelInput}/least_watched/limit={limit}")
    public List<VideoDTO> leastByViews(@PathVariable String channelInput,
                                       @PathVariable int limit){
        return videoService.leastViews(channelInput, limit);
    }
}
