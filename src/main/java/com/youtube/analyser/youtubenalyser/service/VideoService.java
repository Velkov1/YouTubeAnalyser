package com.youtube.analyser.youtubenalyser.service;

import com.youtube.analyser.youtubenalyser.client.YouTubeClient;
import com.youtube.analyser.youtubenalyser.exception.*;
import com.youtube.analyser.youtubenalyser.model.channel.ChannelDTO;
import com.youtube.analyser.youtubenalyser.model.channel.ChannelInsightsDTO;
import com.youtube.analyser.youtubenalyser.model.channel.ChannelUploadsCache;
import com.youtube.analyser.youtubenalyser.model.playlist.PlaylistItem;
import com.youtube.analyser.youtubenalyser.model.playlist.PlaylistItemListResponse;
import com.youtube.analyser.youtubenalyser.model.region.RegionCodeCache;
import com.youtube.analyser.youtubenalyser.model.video.*;
import com.youtube.analyser.youtubenalyser.model.videoCategory.VideoCategoryCache;
import com.youtube.analyser.youtubenalyser.model.videoCategory.VideoCategoryResponse;
import com.youtube.analyser.youtubenalyser.repository.ChannelUploadsCacheRepository;
import com.youtube.analyser.youtubenalyser.repository.VideoRepository;
import com.youtube.analyser.youtubenalyser.repository.YTVideoSnapshotRepository;
import com.youtube.analyser.youtubenalyser.search.SearchListResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@AllArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final YTVideoSnapshotRepository snapshotRepository;
    private final YouTubeClient youTubeClient;
    private final ChannelService channelService;
    private final RegionCodeCache regionCodeCache;
    private final VideoCategoryCache videoCategoryCache;
    private final ChannelUploadsCacheRepository channelUploadsCacheRepository;
    private final String parts = "snippet, contentDetails, status, statistics, hasPaidProductPlacementDetails";

    //fetching data with video id
    //provided the user enters either a video link or a video id
    public VideoDTO getVideoDataWithId(String videoContent){
        return toYTVideoDTO(getOrCreateVideo(toVideoId(videoContent)));
    }
    private YTVideo getOrCreateVideo(String videoId){
        return videoRepository.findByVideoId(videoId)
                .filter(this::isNew)
                .orElseGet(() -> fetchAndSaveVideo(videoId));
    }

    private boolean isNew(YTVideo video){
        return video.getSavedToRepo() != null && video.getSavedToRepo().isAfter(Instant.now().minus(Duration.ofHours(24)));
    }

    private YTVideo fetchAndSaveVideo(String videoId){
        VideoResponse response = youTubeClient.getVideoDataWithVideoId(parts, videoId);
        YTVideo video = toYTVideo(response);
        snapshotRepository.save(new YTVideoSnapshot(video.getVideoId(), video.getViewCount(), video.getLikeCount(), video.getCommentCount()));
        return videoRepository.save(video);
    }
    private YTVideo toYTVideo(VideoResponse response){
        return new YTVideo(
                response.getId(),
                response.getSnippet().getPublishedAt(), response.getSnippet().getChannelId(), response.getSnippet().getVideoTitle(), response.getSnippet().getVideoDescription(), response.getSnippet().getThumbnails().values().stream().toList(),response.getSnippet().getChannelTitle(), response.getSnippet().getTags(), response.getSnippet().getCategoryId(), response.getSnippet().getLiveBroadcastContent(), response.getSnippet().getDefaultLanguage(),
                response.getContentDetails().getDuration(), response.getContentDetails().getDefinition(), response.getContentDetails().getCaption(), response.getContentDetails().getRegionRestriction().getAllowed(), response.getContentDetails().getRegionRestriction().getBlocked(),
                response.getStatus().getPrivacyStatus(),response.getStatus().isMadeForKids(),
                response.getStatistics().getViewCount(), response.getStatistics().getLikeCount(), response.getStatistics().getCommentCount(),
                response.getHasPaidProductPlacementDetails().isHasPaidProductPlacement()
        );
    }
    private VideoDTO toYTVideoDTO(YTVideo video){
        long views = video.getViewCount();
        double likeToViewRatio = views == 0 ? 0.0 : video.getLikeCount() / (double) views;
        double commentToViewRatio = views == 0 ? 0.0 : video.getCommentCount() / (double) views;
        long durationInSeconds = durationInSeconds(video.getDuration());
        return new VideoDTO(
                video.getVideoId(),
                video.getSavedToRepo(),
                video.getPublishedAt(), video.getChannelId(), video.getVideoTitle(), video.getVideoDescription(), video.getThumbnails(), video.getChannelTitle(), video.getTags(), video.getCategoryId(), video.getLiveBroadcastContent(), video.getDefaultLanguage(),
                video.getDuration(), video.getDefinition(), video.getCaption(),
                video.getAllowed(), video.getBlocked(),
                video.getPrivacyStatus(), video.isMadeForKids(),
                video.getViewCount(), video.getLikeCount(), video.getCommentCount(),
                video.isHasPaidProductPlacement(),
                likeToViewRatio, commentToViewRatio, durationInSeconds
        );
    }

    private long durationInSeconds(String rawDuration){
        if(rawDuration == null || rawDuration.isBlank()){
            return 0;
        }
        try{
            return Duration.parse(rawDuration).getSeconds();
        } catch (DateTimeParseException e){
            return 0;
        }
    }

    // fetch by multiple video ids
    //provided the user enters either a video link or a video id
    public List<VideoDTO> getManyVideos(List<String> videoContent){
        List<String> ids = videoContent.stream()
                .map(this::toVideoId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if(ids.isEmpty()){
            return List.of();
        }
        return fetchByIds(ids).stream()
                .map(this::toYTVideoDTO)
                .toList();
    }

    public String toVideoId(String data){
        if(data == null || data.isBlank()){
            throw new NoSuchVideoException("No video input provided");
        }
        String cleaned = data.trim();
        if(!cleaned.contains("/")){
            return validate(cleaned, data);
        }
        if(cleaned.contains("watch?v=")){
            cleaned = cleaned.substring(cleaned.indexOf("watch?v=") + 8);
        }
        else{
            cleaned = cleaned.substring(cleaned.lastIndexOf("/" + 1));
        }
        return validate(cleaned.split("[?&#]")[0], data);
    }

    private String validate(String id, String original) {
        if (id.length() != 11) {
            throw new NoSuchVideoException("Not a valid video id or URL: " + original);
        }
        return id;
    }

    private List<YTVideo> fetchByIds(List<String> ids){
        List<YTVideo> result = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for(String id : ids){
            videoRepository.findByVideoId(id).filter(this::isNew)
                    .ifPresentOrElse(result::add, () -> missing.add(id));
        }
        if(!missing.isEmpty()){
            for(VideoResponse resp : youTubeClient.getMultipleVideoData(parts, String.join(",", missing))){
                result.add(videoRepository.save(toYTVideo(resp)));
                YTVideo video = toYTVideo(resp);
                snapshotRepository.save(new YTVideoSnapshot(video.getVideoId(), video.getViewCount(), video.getLikeCount(), video.getCommentCount()));
            }
        }
        return result;
    }


    //fetch the playlist with all videos of a channel
    public List<VideoDTO> getChannelVideos(String channelInput){
        String channelId = channelService.toChannelId(channelInput);
        if(channelId == null){
            throw new NoSuchChannelException("Could not find channel " + channelId);
        }
        List<String> videoIds = channelUploadsCacheRepository.findById(channelId)
                .filter(c -> c.getLastFetchedAt().isAfter(Instant.now().minus(Duration.ofHours(24))))
                .map(ChannelUploadsCache::getVideoIds)
                .orElseGet(() -> {
                    String playlistId = channelService.getUploadsPlaylistId(channelId);
                    List<String> ids =  fetchVideoIdsFromPlaylistId(playlistId);
                    channelUploadsCacheRepository.save(new ChannelUploadsCache(channelId, ids));
                    return ids;
                }
                );
        return getManyVideos(videoIds);
    }

    private List<String> fetchVideoIdsFromPlaylistId(String playlistId){
        List<String> videoIds = new ArrayList<>();
        String nextPageToken = null;
        do{
            PlaylistItemListResponse response = youTubeClient.getPlaylistItemResponse(playlistId, nextPageToken);
            for(PlaylistItem item : response.getItems()){
                videoIds.add(item.getSnippet().getResourceId().getVideoId());
            }
            nextPageToken = response.getNextPageToken();
        }while(nextPageToken != null);
        return videoIds;
    }

    // sort the first {limit} videos by views of the given channel
    public List<VideoDTO> topByViews(String channelInput, int limit){
        if(limit == 0){
            return List.of();
        }
        if(limit < 0){
            throw new IllegalArgumentException();
        }
        getChannelVideos(channelInput);
        String channelId = channelService.toChannelId(channelInput);
        return videoRepository.findByChannelIdOrderByViewCountDesc(channelId, PageRequest.of(0, limit))
                .stream()
                .map(this::toYTVideoDTO)
                .toList();
    }

    public List<VideoDTO> leastViews(String channelInput, int limit){
        if(limit == 0){
            return List.of();
        }
        if(limit < 0){
            throw new IllegalArgumentException();
        }
        getChannelVideos(channelInput);
        String channelId = channelService.toChannelId(channelInput);
        return videoRepository.findByChannelIdOrderByViewCountAsc(channelId, PageRequest.of(0, limit))
                .stream()
                .map(this::toYTVideoDTO)
                .toList();
    }

    public List<VideoDTO> recentVideos(String channelInput, int limit){
        if(limit == 0){
            return List.of();
        }
        if(limit < 0){
            throw new IllegalArgumentException();
        }
        getChannelVideos(channelInput);
        String channelId = channelService.toChannelId(channelInput);
        return videoRepository.findByChannelIdOrderByPublishedAtDesc(channelId, PageRequest.of(0, limit))
                .stream()
                .map(this::toYTVideoDTO)
                .toList();
    }

    public List<VideoDTO> oldestVideos(String channelInput, int limit){
        if(limit == 0){
            return List.of();
        }
        if(limit < 0){
            throw new IllegalArgumentException();
        }
        getChannelVideos(channelInput);
        String channelId = channelService.toChannelId(channelInput);
        return videoRepository.findByChannelIdOrderByPublishedAtAsc(channelId, PageRequest.of(0, limit))
                .stream()
                .map(this::toYTVideoDTO)
                .toList();
    }


    // to define the best performers of a given channel I have to create a formula that will define the tier based on the videos, length of the video, comment and like count
    // the most important thing is the total watched time from all viewers(we assume that each viewer watched from start to the end and the other who didn't make it to the end aren't counted as viewers)
    //total score = (viewers * videoLength) * (1 + (likes/views) * 10 + (comments/views) * 30) + (if the video has paidProductPlacement we add also 0.30 to the coefficient))
    public List<VideoDTO> bestPerformersFromChannelVideos(String channelInput, int limit){
        if(limit == 0){
            return List.of();
        }
        if(limit < 0){
            throw new IllegalArgumentException();
        }

        List<VideoDTO> videos = getChannelVideos(channelInput);
        return videos.stream()
                .sorted(Comparator.comparingDouble(this::getVideoScore).reversed())
                .limit(limit)
                .toList();
    }

    private Double getVideoScore(VideoDTO video){
        long views = video.getViewCount();
        if(views == 0){
            return 0.0;
        }
        double minutes = durationInMinutes(video);
        long likes = video.getLikeCount();
        long comments = video.getCommentCount();
        double coefficient = 1
                + (likes / (double)views) * 10
                + (comments / (double) views) * 30
                +(video.isHasPaidProductPlacement() ? 0.30 : 0.0);
        return minutes * views * coefficient;
    }

    private double durationInMinutes(VideoDTO video){
        return video.getDurationInSeconds() / 60.0;
    }

    public List<VideoDTO> worstPerformersFromChannelVideos(String channelInput, int limit){
        if(limit == 0){
            return List.of();
        }
        if(limit < 0){
            throw new IllegalArgumentException();
        }

        List<VideoDTO> videos = getChannelVideos(channelInput);
        return videos.stream()
                .sorted(Comparator.comparingDouble(this::getVideoScore))
                .limit(limit)
                .toList();
    }

    // returns all videos uploaded between startDate and endDate
    public List<VideoDTO> publishedBetween(String channelInput, Instant startDate, Instant endDate){
        String channelId = channelService.toChannelId(channelInput);
        List<VideoDTO> videos = getChannelVideos(channelInput);
        return videoRepository.findByChannelIdAndPublishedAtBetween(channelId, startDate, endDate);
    }

    public List<VideoDTO> publishedBefore(String channelInput, Instant date){
        String channelId = channelService.toChannelId(channelInput);
        List<VideoDTO> videos = getChannelVideos(channelInput);
        return videoRepository.findByChannelIdAndPublishedAtBefore(channelId, date);
    }

    public List<VideoDTO> publishedAfter(String channelInput, Instant date){
        String channelId = channelService.toChannelId(channelInput);
        List<VideoDTO> videos = getChannelVideos(channelInput);
        return videoRepository.findByChannelIdAndPublishedAtAfter(channelId, date);
    }

    public ChannelInsightsDTO getChannelInsights(String channelInput){
        String channelId = channelService.toChannelId(channelInput);
        ChannelDTO channel = channelService.getChannelData(channelId);
        List<VideoDTO> videos = getChannelVideos(channelId);
        return new ChannelInsightsDTO(
                channel,
                averageVideoLength(videos),
                uploadDayDistribution(videos),
                uploadHourDistribution(videos, channel),
                viewConcentration(videos)
        );
    }

    private double averageVideoLength(List<VideoDTO> videos){
        if(videos.isEmpty()){
            return 0.0;
        }
        double totalLength = 0.0;
        for(VideoDTO video : videos){
            totalLength += durationInMinutes(video);
        }
        return totalLength / (double) videos.size();
    }

    private Map<DayOfWeek, Long> uploadDayDistribution(List<VideoDTO> videos){
        Map<DayOfWeek, Long> counts = new EnumMap<>(DayOfWeek.class);
        for(DayOfWeek day : DayOfWeek.values()){
            counts.put(day, 0L);
        }

        for(VideoDTO video : videos){
            if(video.getPublishedAt() == null){
                continue;
            }
            DayOfWeek day = video.getPublishedAt().atZone(ZoneOffset.UTC)
                    .getDayOfWeek();
            counts.put(day, counts.get(day) + 1);
        }
        return counts;
    }

    private Map<Integer, Long> uploadHourDistribution(List<VideoDTO> videos, ChannelDTO channel){
        Map<Integer, Long> counts = new TreeMap<>();
        for (int hour = 0; hour < 24; hour++) {
            counts.put(hour, 0L);
        }
        for(VideoDTO video : videos){
            if (video.getPublishedAt() == null) {
                continue;
            }
            int hour = video.getPublishedAt()
                    .atZone(channel.getCountry() != null
                            ? ZoneId.of(ZoneId.SHORT_IDS.getOrDefault(channel.getCountry(), "UTC"))
                            : ZoneOffset.UTC)
                    .getHour();
            counts.put(hour, counts.get(hour) + 1);
        }
        return counts;
    }

    //returns what part of the total views of the channel comes from the top 10% most watched videos
    private double viewConcentration(List<VideoDTO> videos){
        double totalLength = 0.0;
        for(VideoDTO video : videos){
            totalLength += durationInMinutes(video);
        }
        if(totalLength == 0.0){
            return 0.0;
        }
        int tenPercentVideosCount = (int)(videos.size() * 0.10);
        List<VideoDTO> bestTenPercent = videos.stream()
                .sorted(Comparator.comparingLong(VideoDTO::getViewCount).reversed())
                .limit(tenPercentVideosCount)
                .toList();
        double bestLength = 0.0;
        for(VideoDTO video : bestTenPercent){
            bestLength += durationInMinutes(video);
        }

        return bestLength / totalLength;
    }

    public List<VideoDTO> trending(String regionCode){
        if(!regionCodeCache.isValid(regionCode)){
            throw new InvalidRegionException("Invalid region code: " + regionCode);
        }
        return youTubeClient.getTrending(parts, regionCode).stream()
                .map(this::toYTVideo)
                .map(this::toYTVideoDTO)
                .toList();
    }

    public List<VideoDTO> trendingInCategory(String regionCode, String categoryId){
        if(!regionCodeCache.isValid(regionCode)){
            throw new InvalidRegionException("Invalid region code: " + regionCode);
        }

        if(!videoCategoryCache.isValid(regionCode, categoryId)){
            throw new InvalidCategoryIdException("Invalid category id:" + categoryId + " for region " + regionCode);
        }
        return youTubeClient.getTrendingByCategory(parts, regionCode, categoryId)
                .stream()
                .map(this::toYTVideo)
                .map(this::toYTVideoDTO)
                .toList();
    }

    //some other more precise statistics
    public VideoInsightsDTO getVideoInsights(String videoInput){
        String videoId = toVideoId(videoInput);
        getOrCreateVideo(videoId);
        VideoDTO video = getVideoDataWithId(videoId);
        return new VideoInsightsDTO(
                video,
                viewIncreaseFromLastSnapshot(videoId),
                averageViewsVelocity(videoId),
                averageCommentVelocity(videoId),
                averageLikesVelocity(videoId),
                momentum(videoId),
                isVideoOutperformingTheChannel(video)
        );
    }

    private long viewIncreaseFromLastSnapshot(String videoId){
        List<YTVideoSnapshot> lastTwo = snapshotRepository.findFirst2ByVideoIdOrderByFetchedAtDesc(videoId);
        if(lastTwo.size() < 2){
            return 0;
        }
        return lastTwo.get(0).getViewCount() - lastTwo.get(1).getViewCount();
    }

    private double averageViewsVelocity(String videoId){
        List<YTVideoSnapshot> list = getFirstAndLast(videoId);
        if(list.isEmpty()){
            return 0.0;
        }
        YTVideoSnapshot first = list.get(0);
        YTVideoSnapshot last = list.get(1);
        double daysElapsed = daysElapsed(first.getFetchedAt(), last.getFetchedAt());
        if(daysElapsed <= 0){
            return 0.0;
        }
        return (last.getViewCount() - first.getViewCount()) / daysElapsed;
    }



    private List<YTVideoSnapshot> getFirstAndLast(String videoId){
        Optional<YTVideoSnapshot> firstOpt = snapshotRepository.findFirstByVideoIdOrderByFetchedAtAsc(videoId);
        Optional<YTVideoSnapshot> lastOpt = snapshotRepository.findFirstByVideoIdOrderByFetchedAtDesc(videoId);
        if(firstOpt.isEmpty() || lastOpt.isEmpty()){
            return List.of();
        }
        YTVideoSnapshot first = firstOpt.get();
        YTVideoSnapshot last = lastOpt.get();
        List<YTVideoSnapshot> list = new ArrayList<>();
        list.add(first);
        list.add(last);
        return list;
    }

    private double daysElapsed(Instant startDate, Instant endDate){
        return Duration.between(startDate, endDate).toSeconds()/86400.0;

    }
    private double averageCommentVelocity(String videoId){
        List<YTVideoSnapshot> list = getFirstAndLast(videoId);
        if(list.isEmpty()){
            return 0.0;
        }
        YTVideoSnapshot first = list.get(0);
        YTVideoSnapshot last = list.get(1);
        double daysElapsed = daysElapsed(first.getFetchedAt(), last.getFetchedAt());
        if(daysElapsed <= 0){
            return 0.0;
        }
        return (last.getCommentCount() - first.getCommentCount())/daysElapsed;

    }

    private double averageLikesVelocity(String videoId){
        List<YTVideoSnapshot> list = getFirstAndLast(videoId);
        if(list.isEmpty()){
            return 0.0;
        }
        YTVideoSnapshot first = list.get(0);
        YTVideoSnapshot last = list.get(1);
        double daysElapsed = daysElapsed(first.getFetchedAt(), last.getFetchedAt());
        if(daysElapsed <= 0){
            return 0.0;
        }
        return (last.getLikeCount() - first.getLikeCount())/daysElapsed;
    }

    // return the momentum difference of the views increase from the last 2 snapshots compared to the last 2 before that
    private double momentum(String videoId){
        List<YTVideoSnapshot> lastTwoSnapshots = snapshotRepository.findFirst4ByVideoIdOrderByFetchedAtDesc(videoId);
        if(lastTwoSnapshots.size() < 4){
            return 0;
        }
        long firstDifference = getDifferenceBetweenSnapshots(lastTwoSnapshots.get(0), lastTwoSnapshots.get(1));
        long secondDifference = getDifferenceBetweenSnapshots(lastTwoSnapshots.get(2), lastTwoSnapshots.get(3));
        double firstDaysElapsed = daysElapsed(lastTwoSnapshots.get(1).getFetchedAt(), lastTwoSnapshots.get(0).getFetchedAt());
        if(firstDaysElapsed <= 0){
            return 0.0;
        }
        double secondDaysElapsed = daysElapsed(lastTwoSnapshots.get(3).getFetchedAt(), lastTwoSnapshots.get(2).getFetchedAt());
        if(secondDaysElapsed <= 0){
            return 0.0;
        }
        double averageVelocityFirst =  firstDifference / firstDaysElapsed;
        double averageVelocitySecond = secondDifference / secondDaysElapsed;
        return averageVelocityFirst - averageVelocitySecond;
    }


    private long getDifferenceBetweenSnapshots(YTVideoSnapshot first, YTVideoSnapshot last){
        return first.getViewCount() - last.getViewCount();
    }

    //return list of video snapshots ever made for the given video
    public List<YTVideoSnapshotDTO> growth(String videoInput){
        String videoId = toVideoId(videoInput);
        return snapshotRepository.findByVideoIdOrderByFetchedAtDesc(videoId).stream()
                .map(this::toSnapshotDTO)
                .toList();
    }

    private YTVideoSnapshotDTO toSnapshotDTO(YTVideoSnapshot snapshot){
        return new YTVideoSnapshotDTO(
                snapshot.getVideoId(),
                snapshot.getViewCount(),
                snapshot.getLikeCount(),
                snapshot.getCommentCount(),
                snapshot.getFetchedAt()
        );
    }

    //return how much time is expected to reach the target views of the video
    public double daysToMilestone(String videoInput, long target){
        String videoId = toVideoId(videoInput);
        VideoDTO video = getVideoDataWithId(videoId);
        long currentViews = video.getViewCount();
        if(target <= currentViews){
            return 0.0;
        }
        double averageViewsPerDay = averageViewsVelocity(videoId);
        if(averageViewsPerDay <= 0.0){
            throw new InsufficientDataException("Not enough snapshot history to estimate a growth rate for video " + videoId);
        }
        return (target - currentViews) / averageViewsPerDay;
    }


    private boolean isVideoOutperformingTheChannel(VideoDTO video){
        List<VideoDTO> channelVideos = getChannelVideos(video.getChannelId());
        if(channelVideos.isEmpty()){
            return false;
        }
        double averageScore = channelVideos.stream()
                .mapToDouble(this::getVideoScore)
                .average()
                .orElse(0.0);
        return getVideoScore(video) > averageScore;
    }

    // returns the videos with the highest momentum difference
    public List<VideoDTO> risingVideos(String channelInput, int limit){
        if(limit <= 0){
            return List.of();
        }
        List<VideoDTO> channelVideos = getChannelVideos(channelInput);
        if(channelVideos.isEmpty()){
            return List.of();
        }
        return channelVideos.stream()
                .sorted(Comparator.comparingDouble((VideoDTO v) -> momentum(v.getVideoId())).reversed())
                .limit(limit)
                .toList();
    }


    public void refreshSnapshot(String videoId){
        fetchAndSaveVideo(videoId);
    }

    public List<VideoDTO> searchVideos(String searchQuery){
        SearchListResponse response = youTubeClient.search(searchQuery, "video", null);
        List<String> ids = response.getItems().stream()
                .map(item -> item.getId().getVideoId())
                .toList();
        return getManyVideos(ids);
    }



}
