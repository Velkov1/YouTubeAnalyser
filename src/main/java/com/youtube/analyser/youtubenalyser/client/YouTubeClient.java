package com.youtube.analyser.youtubenalyser.client;

import com.youtube.analyser.youtubenalyser.exception.NoSuchChannelException;
import com.youtube.analyser.youtubenalyser.exception.NoSuchVideoException;
import com.youtube.analyser.youtubenalyser.model.ChannelListResponse;
import com.youtube.analyser.youtubenalyser.model.ChannelResponse;
import com.youtube.analyser.youtubenalyser.model.VideoListResponse;
import com.youtube.analyser.youtubenalyser.model.VideoResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

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
}
