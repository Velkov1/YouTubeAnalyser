package com.youtube.analyser.youtubenalyser.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class ContentDetails {

    private RelatedPlaylists relatedPlaylists;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class RelatedPlaylists{
        private String uploads; // this returns the playlist id of a playlist,that contains all the uploaded vids of the channel
    }
}
