package com.youtube.analyser.youtubenalyser.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoContentDetails {
    private String duration;
    private String definition;
    private String caption;
    private RegionRestriction regionRestriction;
}
