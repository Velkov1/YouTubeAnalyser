package com.youtube.analyser.youtubenalyser.model.channel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class ChannelListResponse {
    private List<ChannelResponse> items;
}
