package com.youtube.analyser.youtubenalyser.client;

import com.youtube.analyser.youtubenalyser.exception.NoSuchChannelException;
import com.youtube.analyser.youtubenalyser.exception.NoSuchVideoException;
import com.youtube.analyser.youtubenalyser.model.channel.ChannelListResponse;
import com.youtube.analyser.youtubenalyser.model.channel.ChannelResponse;
import com.youtube.analyser.youtubenalyser.model.comment.CommentThreadListResponse;
import com.youtube.analyser.youtubenalyser.model.playlist.PlaylistItemListResponse;
import com.youtube.analyser.youtubenalyser.model.region.I18nRegionListResponse;
import com.youtube.analyser.youtubenalyser.model.region.I18nRegionsResponse;
import com.youtube.analyser.youtubenalyser.model.video.VideoListResponse;
import com.youtube.analyser.youtubenalyser.model.video.VideoResponse;
import com.youtube.analyser.youtubenalyser.model.videoCategory.VideoCategoryListResponse;
import com.youtube.analyser.youtubenalyser.model.videoCategory.VideoCategoryResponse;
import com.youtube.analyser.youtubenalyser.search.SearchListResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
public class YouTubeClient {
    private final RestClient restClient;

    @Value("${youTube.api-key}")
    private String key;

    public YouTubeClient(@Qualifier("youTubeRestClient") RestClient restClient){
     this.restClient = restClient;
    }

    //Channels mapping
    public ChannelResponse getChannelDataWithChannelId(String parts, String channelId){
        ChannelListResponse response =  restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/channels")
                            .queryParam("part", parts)
                            .queryParam("id", channelId)
                            .queryParam("key", key)
                            .build())
                    .retrieve()
                    .body(ChannelListResponse.class);
        if(response == null || response.getItems()==null ||response.getItems().isEmpty()){
            throw new NoSuchChannelException("No channel found with id " + channelId);
        }
        return response.getItems().get(0);
    }

    public ChannelResponse getChannelDataWithHandle(String parts, String handle){
        ChannelListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/channels")
                        .queryParam("part", parts)
                        .queryParam("forHandle", handle)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(ChannelListResponse.class);
        if(response == null || response.getItems()==null ||response.getItems().isEmpty()){
            throw new NoSuchChannelException("No channel found with handle " + handle);
        }
        return response.getItems().get(0);
    }

    public List<ChannelResponse> getMultipleChannelData(String parts, String ids){
        ChannelListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/channels")
                        .queryParam("part", parts)
                        .queryParam("id", ids)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(ChannelListResponse.class);

        if(response == null || response.getItems() == null){
            return List.of();
        }
        return response.getItems();
    }


    //VIDEO

    public VideoResponse getVideoDataWithVideoId(String parts, String videoId){
        VideoListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", parts)
                        .queryParam("id", videoId)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(VideoListResponse.class);
        if(response == null || response.getItems()== null ||response.getItems().isEmpty()){
            throw new NoSuchVideoException("No video found with id " + videoId);
        }
        return response.getItems().get(0);
    }
    public List<VideoResponse> getMultipleVideoData(String parts, String ids){
        VideoListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", parts)
                        .queryParam("id", ids)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(VideoListResponse.class);
        if(response == null || response.getItems() == null){
            return  List.of();
        }
        return response.getItems();
    }

    public PlaylistItemListResponse getPlaylistItemResponse(String playlistId, String nextPageToken){
        PlaylistItemListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/playlistItems")
                        .queryParam("part", "snippet")
                        .queryParam("playlistId", playlistId)
                        .queryParam("maxResults", 50)
                        .queryParamIfPresent("pageToken", Optional.ofNullable(nextPageToken))
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(PlaylistItemListResponse.class);
        if(response == null || response.getItems() == null){
            response = new PlaylistItemListResponse();
            response.setItems(List.of());
        }
        return response;
    }

    public I18nRegionListResponse getI18nRegions(){
        I18nRegionListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/i18nRegions")
                        .queryParam("part", "snippet")
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(I18nRegionListResponse.class);
        if(response == null || response.getItems() == null){
            response = new I18nRegionListResponse();
            response.setItems(List.of());
        }
        return response;
    }

    public List<VideoResponse> getTrending(String parts, String regionCode){
        VideoListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", parts)
                        .queryParam("chart", "mostPopular")
                        .queryParam("regionCode", regionCode)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(VideoListResponse.class);
        if(response == null || response.getItems() == null){
            return  List.of();
        }
        return response.getItems();
    }

    public List<VideoResponse> getTrendingByCategory(String parts, String regionCode, String categoryId){
        VideoListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", parts)
                        .queryParam("chart", "mostPopular")
                        .queryParam("regionCode", regionCode)
                        .queryParam("videoCategoryId", categoryId)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(VideoListResponse.class);
        if(response == null || response.getItems() == null){
            return  List.of();
        }
        return response.getItems();
    }

    public VideoCategoryListResponse getVideoCategories(String regionCode){
        VideoCategoryListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videoCategories")
                        .queryParam("part", "snippet")
                        .queryParam("regionCode", regionCode)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(VideoCategoryListResponse.class);
        if(response == null || response.getItems() == null){
            response = new VideoCategoryListResponse();
            response.setItems(List.of());
        }
        return response;
    }

    public CommentThreadListResponse getCommentThreadsByVideo(String parts, String videoId, String nextPageToken){
        CommentThreadListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/commentThreads")
                        .queryParam("part", parts)
                        .queryParam("videoId", videoId)
                        .queryParam("maxResults", 100)
                        .queryParamIfPresent("pageToken", Optional.ofNullable(nextPageToken))
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(CommentThreadListResponse.class);
        if(response == null || response.getItems() == null){
            response = new CommentThreadListResponse();
            response.setItems(List.of());
        }
        return response;
    }

    public SearchListResponse search(String searchQuery, String type, String nextPageToken){
        SearchListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("search")
                        .queryParam("part", "snippet")
                        .queryParam("maxResults", 25)
                        .queryParam("q", searchQuery)
                        .queryParam("type", type)
                        .queryParamIfPresent("pageToken", Optional.ofNullable(nextPageToken))
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .body(SearchListResponse.class);

        if(response == null || response.getItems() == null){
            response = new SearchListResponse();
            response.setItems(List.of());
        }
        return response;
    }
}
