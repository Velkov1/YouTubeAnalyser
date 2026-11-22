package com.youtube.analyser.youtubeanalyser.client;

import com.youtube.analyser.youtubeanalyser.exception.NoSuchChannelException;
import com.youtube.analyser.youtubeanalyser.exception.NoSuchVideoException;
import com.youtube.analyser.youtubeanalyser.model.channel.ChannelListResponse;
import com.youtube.analyser.youtubeanalyser.model.channel.ChannelResponse;
import com.youtube.analyser.youtubeanalyser.model.comment.CommentThreadListResponse;
import com.youtube.analyser.youtubeanalyser.model.playlist.PlaylistItemListResponse;
import com.youtube.analyser.youtubeanalyser.model.region.I18nRegionListResponse;
import com.youtube.analyser.youtubeanalyser.model.video.VideoListResponse;
import com.youtube.analyser.youtubeanalyser.model.video.VideoResponse;
import com.youtube.analyser.youtubeanalyser.model.videoCategory.VideoCategoryListResponse;
import com.youtube.analyser.youtubeanalyser.search.SearchListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Component
public class YouTubeClient {
    private static final int MAX_ATTEMPTS = 3;

    private final RestClient restClient;

    @Value("${youTube.api-key}")
    private String key;

    public YouTubeClient(@Qualifier("youTubeRestClient") RestClient restClient){
     this.restClient = restClient;
    }

    private <T> T withRetry(Supplier<T> call){
        for(int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++){
            try{
                return call.get();
            } catch (HttpClientErrorException e){
                throw e;
            } catch (RuntimeException e){
                if(attempt == MAX_ATTEMPTS){
                    throw e;
                }
                log.warn("Transient failure calling YouTube API (attempt {}/{}): {}", attempt, MAX_ATTEMPTS, e.getMessage());
                try{
                    Thread.sleep(300L * attempt);
                } catch (InterruptedException interrupted){
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    //Channels mapping
    public ChannelResponse getChannelDataWithChannelId(String parts, String channelId){
        ChannelListResponse response = withRetry(() -> restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/channels")
                            .queryParam("part", parts)
                            .queryParam("id", channelId)
                            .queryParam("key", key)
                            .build())
                    .retrieve()
                    .body(ChannelListResponse.class));
        if(response == null || response.getItems()==null ||response.getItems().isEmpty()){
            throw new NoSuchChannelException("No channel found with id " + channelId);
        }
        return response.getItems().get(0);
    }

    public ChannelResponse getChannelDataWithHandle(String parts, String handle){
        ChannelListResponse response = withRetry(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/channels")
                        .queryParam("part", parts)
                        .queryParam("forHandle", handle)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(ChannelListResponse.class));
        if(response == null || response.getItems()==null ||response.getItems().isEmpty()){
            throw new NoSuchChannelException("No channel found with handle " + handle);
        }
        return response.getItems().get(0);
    }

    public List<ChannelResponse> getMultipleChannelData(String parts, String ids){
        ChannelListResponse response = withRetry(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/channels")
                        .queryParam("part", parts)
                        .queryParam("id", ids)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(ChannelListResponse.class));

        if(response == null || response.getItems() == null){
            return List.of();
        }
        return response.getItems();
    }


    //VIDEO

    public VideoResponse getVideoDataWithVideoId(String parts, String videoId){
        VideoListResponse response = withRetry(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", parts)
                        .queryParam("id", videoId)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(VideoListResponse.class));
        if(response == null || response.getItems()== null ||response.getItems().isEmpty()){
            throw new NoSuchVideoException("No video found with id " + videoId);
        }
        return response.getItems().get(0);
    }
    public List<VideoResponse> getMultipleVideoData(String parts, String ids){
        VideoListResponse response = withRetry(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", parts)
                        .queryParam("id", ids)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(VideoListResponse.class));
        if(response == null || response.getItems() == null){
            return  List.of();
        }
        return response.getItems();
    }

    public PlaylistItemListResponse getPlaylistItemResponse(String playlistId, String nextPageToken){
        PlaylistItemListResponse response = withRetry(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/playlistItems")
                        .queryParam("part", "snippet")
                        .queryParam("playlistId", playlistId)
                        .queryParam("maxResults", 50)
                        .queryParamIfPresent("pageToken", Optional.ofNullable(nextPageToken))
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(PlaylistItemListResponse.class));
        if(response == null || response.getItems() == null){
            response = new PlaylistItemListResponse();
            response.setItems(List.of());
        }
        return response;
    }

    public I18nRegionListResponse getI18nRegions(){
        I18nRegionListResponse response = withRetry(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/i18nRegions")
                        .queryParam("part", "snippet")
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(I18nRegionListResponse.class));
        if(response == null || response.getItems() == null){
            response = new I18nRegionListResponse();
            response.setItems(List.of());
        }
        return response;
    }

    public List<VideoResponse> getTrending(String parts, String regionCode){
        VideoListResponse response = withRetry(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", parts)
                        .queryParam("chart", "mostPopular")
                        .queryParam("regionCode", regionCode)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(VideoListResponse.class));
        if(response == null || response.getItems() == null){
            return  List.of();
        }
        return response.getItems();
    }

    public List<VideoResponse> getTrendingByCategory(String parts, String regionCode, String categoryId){
        VideoListResponse response = withRetry(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", parts)
                        .queryParam("chart", "mostPopular")
                        .queryParam("regionCode", regionCode)
                        .queryParam("videoCategoryId", categoryId)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(VideoListResponse.class));
        if(response == null || response.getItems() == null){
            return  List.of();
        }
        return response.getItems();
    }

    public VideoCategoryListResponse getVideoCategories(String regionCode){
        VideoCategoryListResponse response = withRetry(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videoCategories")
                        .queryParam("part", "snippet")
                        .queryParam("regionCode", regionCode)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(VideoCategoryListResponse.class));
        if(response == null || response.getItems() == null){
            response = new VideoCategoryListResponse();
            response.setItems(List.of());
        }
        return response;
    }

    public CommentThreadListResponse getCommentThreadsByVideo(String parts, String videoId, String nextPageToken){
        CommentThreadListResponse response = withRetry(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/commentThreads")
                        .queryParam("part", parts)
                        .queryParam("videoId", videoId)
                        .queryParam("maxResults", 100)
                        .queryParamIfPresent("pageToken", Optional.ofNullable(nextPageToken))
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(CommentThreadListResponse.class));
        if(response == null || response.getItems() == null){
            response = new CommentThreadListResponse();
            response.setItems(List.of());
        }
        return response;
    }

    public SearchListResponse search(String searchQuery, String type, String nextPageToken){
        SearchListResponse response = withRetry(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("part", "snippet")
                        .queryParam("maxResults", 25)
                        .queryParam("q", searchQuery)
                        .queryParam("type", type)
                        .queryParamIfPresent("pageToken", Optional.ofNullable(nextPageToken))
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(SearchListResponse.class));

        if(response == null || response.getItems() == null){
            response = new SearchListResponse();
            response.setItems(List.of());
        }
        return response;
    }
}
