package com.youtube.analyser.youtubeanalyser.controller;

import com.youtube.analyser.youtubeanalyser.model.channel.ChannelInsightsDTO;
import com.youtube.analyser.youtubeanalyser.model.video.VideoDTO;
import com.youtube.analyser.youtubeanalyser.model.video.VideoInsightsDTO;
import com.youtube.analyser.youtubeanalyser.model.video.YTVideoSnapshotDTO;
import com.youtube.analyser.youtubeanalyser.service.VideoService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
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

    @GetMapping("/{videoInput}/video_insights")
    public VideoInsightsDTO getVideoInsights(@PathVariable String videoInput){
        return videoService.getVideoInsights(videoInput);
    }

    @GetMapping("/multiple_search")
    public List<VideoDTO> getManyVideos(@RequestParam List<String> videoInputs){
        return videoService.getManyVideos(videoInputs);
    }

    @GetMapping("/{channelInput}/all")
    public List<VideoDTO> getChannelVideos(@PathVariable String channelInput){
        return videoService.getChannelVideos(channelInput);
    }

    @GetMapping("/{channelInput}/most_watched")
    public List<VideoDTO> topByViews(@PathVariable String channelInput,
                                     @RequestParam int limit){
        return videoService.topByViews(channelInput, limit);
    }

    @GetMapping("/{channelInput}/least_watched")
    public List<VideoDTO> leastByViews(@PathVariable String channelInput,
                                       @RequestParam int limit){
        return videoService.leastViews(channelInput, limit);
    }

    @GetMapping("/{channelInput}/recent")
    public List<VideoDTO> recentVideos(@PathVariable String channelInput,
                                       @RequestParam int limit){
        return videoService.recentVideos(channelInput, limit);
    }
    @GetMapping("/{channelInput}/oldest")
    public List<VideoDTO> oldestVideos(@PathVariable String channelInput,
                                       @RequestParam int limit){
        return videoService.oldestVideos(channelInput, limit);
    }

    @GetMapping("{channelInput}/best_performers")
    public List<VideoDTO> bestPerformers(@PathVariable String channelInput,
                                         @RequestParam int limit){
        return videoService.bestPerformersFromChannelVideos(channelInput, limit);
    }

    @GetMapping("{channelInput}/worst_performers")
    public List<VideoDTO> worstPerformers(@PathVariable String channelInput,
                                          @RequestParam int limit){
        return videoService.worstPerformersFromChannelVideos(channelInput, limit);
    }



    @GetMapping("{channelInput}/published_before")
    public List<VideoDTO> publishedBefore(@PathVariable String channelInput,
                                          @RequestParam Instant date){
        return videoService.publishedBefore(channelInput, date);
    }

    @GetMapping("{channelInput}/published_between")
    public List<VideoDTO> publishedBetween(@PathVariable String channelInput,
                                           @RequestParam("start_date") Instant startDate,
                                           @RequestParam("end_date") Instant endDate){
        return videoService.publishedBetween(channelInput, startDate, endDate);
    }

    @GetMapping("{channelInput}/published_after")
    public List<VideoDTO> publishedAfter(@PathVariable String channelInput,
                                         @RequestParam Instant date){
        return videoService.publishedAfter(channelInput, date);
    }

    @GetMapping("{channelInput}/insights")
    public ChannelInsightsDTO getChannelInsights(@PathVariable String channelInput){
        return videoService.getChannelInsights(channelInput);
    }

    @GetMapping("/trending")
    public List<VideoDTO> trending(@RequestParam("region") String regionCode){
        return videoService.trending(regionCode);
    }

    @GetMapping(value = "/trending", params = "category")
    public List<VideoDTO> trendingInCategory(@RequestParam("region") String regionCode,
                                             @RequestParam("category") String categoryId){
        return videoService.trendingInCategory(regionCode, categoryId);
    }

    @GetMapping("/{videoInput}/growth")
    public List<YTVideoSnapshotDTO> growth(@PathVariable String videoInput){
        return videoService.growth(videoInput);
    }

    @GetMapping("/{videoInput}/days_to_milestone")
    public double daysToMilestone(@PathVariable String videoInput,
                                  @RequestParam long target){
        return videoService.daysToMilestone(videoInput, target);
    }

    @GetMapping("/{channelInput}/rising_videos")
    public List<VideoDTO> risingVideos(@PathVariable String channelInput,
                                       @RequestParam int limit){
        return videoService.risingVideos(channelInput, limit);
    }

    @GetMapping("/search/{searchQuery}")
    public List<VideoDTO> searchVideos(@PathVariable String searchQuery){
        return videoService.searchVideos(searchQuery);
    }


}
