package com.youtube.analyser.youtubeanalyser.model.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.youtube.analyser.youtubeanalyser.model.channel.RegionRestriction;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoContentDetails {
    private String duration;
    private String definition;
    private String caption;
    private RegionRestriction regionRestriction;
}
