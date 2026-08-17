package com.youtube.analyser.youtubenalyser.model.channel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class Status {
    private String privacyStatus;
    private boolean madeForKids;

}
