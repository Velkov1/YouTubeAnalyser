package com.youtube.analyser.youtubenalyser.controller;

import com.youtube.analyser.youtubenalyser.model.channel.ChannelDTO;
import com.youtube.analyser.youtubenalyser.model.channel.YTChannelSnapshotDTO;
import com.youtube.analyser.youtubenalyser.service.ChannelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;

    public ChannelController(ChannelService channelService){
        this.channelService = channelService;
    }

    @GetMapping("/{channelInput}")
    public ChannelDTO getChannelData(@PathVariable String channelInput){
        return channelService.getChannelData(channelInput);
    }

    @GetMapping("/multiple_search")
    public List<ChannelDTO> getManyChannels(@RequestParam List<String> channelInputs){
        return channelService.getManyChannels(channelInputs);
    }

    @GetMapping("/{channelInput}/history")
    public List<YTChannelSnapshotDTO> getHistory(@PathVariable String channelInput){
        return channelService.getHistory(channelInput);
    }

    @GetMapping("/search/{searchQuery}")
    public List<ChannelDTO> searchChannels(@PathVariable String searchQuery){
        return channelService.searchChannels(searchQuery);
    }
}
