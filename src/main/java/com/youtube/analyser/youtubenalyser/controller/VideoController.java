package com.youtube.analyser.youtubenalyser.controller;

import com.youtube.analyser.youtubenalyser.model.channel.ChannelInsightsDTO;
import com.youtube.analyser.youtubenalyser.model.video.VideoDTO;
import com.youtube.analyser.youtubenalyser.model.video.VideoInsightsDTO;
import com.youtube.analyser.youtubenalyser.model.video.YTVideoSnapshotDTO;
import com.youtube.analyser.youtubenalyser.service.VideoService;
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

    @GetMapping("/{channelInput}/recent/limit={limit}")
    public List<VideoDTO> recentVideos(@PathVariable String channelInput,
                                       @PathVariable int limit){
        return videoService.recentVideos(channelInput, limit);
    }
    @GetMapping("/{channelInput}/oldest/limit={limit}")
    public List<VideoDTO> oldestVideos(@PathVariable String channelInput,
                                       @PathVariable int limit){
        return videoService.oldestVideos(channelInput, limit);
    }

    @GetMapping("{channelInput}/best_performers/limit={limit}")
    public List<VideoDTO> bestPerformers(@PathVariable String channelInput,
                                         @PathVariable int limit){
        return videoService.bestPerformersFromChannelVideos(channelInput, limit);
    }

    @GetMapping("{channelInput}/worst_performers/limit={limit}")
    public List<VideoDTO> worstPerformers(@PathVariable String channelInput,
                                          @PathVariable int limit){
        return videoService.worstPerformersFromChannelVideos(channelInput, limit);
    }



    @GetMapping("{channelInput}/published_before/date={date}")
    public List<VideoDTO> publishedBefore(@PathVariable String channelInput,
                                          @PathVariable Instant date){
        return videoService.publishedBefore(channelInput, date);
    }

    @GetMapping("{channelInput}/published_between/start_date={startDate}/end_date={endDate}")
    public List<VideoDTO> publishedBetween(@PathVariable String channelInput,
                                          @PathVariable Instant startDate,
                                           @PathVariable Instant endDate){
        return videoService.publishedBetween(channelInput, startDate, endDate);
    }

    @GetMapping("{channelInput}/published_after/date={date}")
    public List<VideoDTO> publishedAfter(@PathVariable String channelInput,
                                          @PathVariable Instant date){
        return videoService.publishedAfter(channelInput, date);
    }

    @GetMapping("{channelInput}/insights")
    public ChannelInsightsDTO getChannelInsights(@PathVariable String channelInput){
        return videoService.getChannelInsights(channelInput);
    }

    @GetMapping("/trending/region={regionCode}")
    public List<VideoDTO> trending(@PathVariable String regionCode){
        return videoService.trending(regionCode);
    }

    @GetMapping("/trending/region={regionCode}/category={categoryId}")
    public List<VideoDTO> trendingInCategory(@PathVariable String regionCode,
                                   @PathVariable String categoryId){
        return videoService.trendingInCategory(regionCode, categoryId);
    }

    @GetMapping("/{videoInput}/growth")
    public List<YTVideoSnapshotDTO> growth(@PathVariable String videoInput){
        return videoService.growth(videoInput);
    }

    @GetMapping("/{videoInput}/days_to_milestone/target={target}")
    public double daysToMilestone(@PathVariable String videoInput,
                                  @PathVariable long target){
        return videoService.daysToMilestone(videoInput, target);
    }

    @GetMapping("/{channelInput}/rising_videos/limit={limit}")
    public List<VideoDTO> risingVideos(@PathVariable String channelInput,
                                       @PathVariable int limit){
        return videoService.risingVideos(channelInput, limit);
    }

    @GetMapping("/search/{searchQuery}")
    public List<VideoDTO> searchVideos(@PathVariable String searchQuery){
        return videoService.searchVideos(searchQuery);
    }


}
