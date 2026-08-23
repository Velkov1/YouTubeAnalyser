package com.youtube.analyser.youtubenalyser.model.channel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.Map;

@Data
@AllArgsConstructor
public class ChannelInsightsDTO {
    private ChannelDTO channel;
    private double averageVideoLengthMinutes;
    private Map<DayOfWeek, Long> uploadDayDistribution;
    private Map<Integer, Long> uploadHourDistribution;
    private double viewConcentration;
}