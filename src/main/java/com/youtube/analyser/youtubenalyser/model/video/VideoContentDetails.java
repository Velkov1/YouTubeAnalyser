package com.youtube.analyser.youtubenalyser.model.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.youtube.analyser.youtubenalyser.model.channel.RegionRestriction;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoContentDetails {
    private String duration;
    private String definition;
    private String caption;
    private RegionRestriction regionRestriction;
}
