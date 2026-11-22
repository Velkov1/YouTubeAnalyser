package com.youtube.analyser.youtubeanalyser.service;

import com.youtube.analyser.youtubeanalyser.client.YouTubeClient;
import com.youtube.analyser.youtubeanalyser.exception.NoSuchChannelException;
import com.youtube.analyser.youtubeanalyser.model.channel.*;
import com.youtube.analyser.youtubeanalyser.repository.ChannelRepository;
import com.youtube.analyser.youtubeanalyser.repository.YTChannelSnapshotRepository;
import com.youtube.analyser.youtubeanalyser.search.SearchListResponse;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ChannelService {
    private final YouTubeClient youTubeClient;
    private final ChannelRepository channelRepository;
    private final YTChannelSnapshotRepository snapshotRepository;
    private final String parts = "snippet,contentDetails,statistics,status";



    //fetching data with channelId or handle
    public ChannelDTO getChannelData(String channelInput){
        return toChannelDTO(getOrCreateChannel(toChannelId(channelInput)));

    }

    private YTChannel getOrCreateChannel(String channelId){
        return channelRepository.findById(channelId)
                .filter(this::isNew)
                .orElseGet(() -> {
                    try{
                        return fetchAndSaveChannel(channelId);
                    } catch (DataIntegrityViolationException e){
                        return channelRepository.findById(channelId).orElseThrow(() -> e);
                    }
                });
    }

    //if the last enter of data of the channel is older than 24 hours, then the repo has to be updated
    private boolean isNew(YTChannel channel){
        return channel.getSavedToTheRepo() != null && channel.getSavedToTheRepo().isAfter(Instant.now().minus(Duration.ofHours(24)));

    }


    private YTChannel fetchAndSaveChannel(String channelId) throws NoSuchChannelException {
        ChannelResponse response = youTubeClient.getChannelDataWithChannelId(parts, channelId);
        YTChannel channel = toYTChannel(response);
        snapshotRepository.save(new YTChannelSnapshot(channel.getYtId(), channel.getTotalViews(), channel.getSubscribers(), channel.getNumVideos()));
        return channelRepository.save(channel);
    }

    private YTChannel fetchAndSaveChannelByHandle(String handle) throws NoSuchChannelException {
        ChannelResponse response = youTubeClient.getChannelDataWithHandle(parts, handle);
        YTChannel channel = toYTChannel(response);
        snapshotRepository.save(new YTChannelSnapshot(channel.getYtId(), channel.getTotalViews(), channel.getSubscribers(), channel.getNumVideos()));
        return channelRepository.save(channel);
    }

    private YTChannel toYTChannel(ChannelResponse response) {
        return new YTChannel(response.getId(),
                response.getSnippet().getTitle(), response.getSnippet().getDescription(), response.getSnippet().getCustomUrl(), response.getSnippet().getPublishedAt(), response.getSnippet().getCountry(), response.getSnippet().getDefaultLanguage(), response.getSnippet().getThumbnails().values().stream().toList(),
                response.getContentDetails().getRelatedPlaylists().getUploads(),
                response.getStatistics().getViewCount(), response.getStatistics().getSubscriberCount(), response.getStatistics().getVideoCount(),
                response.getStatus().getPrivacyStatus(), response.getStatus().isMadeForKids());
    }

    private ChannelDTO toChannelDTO(YTChannel channel){
        long views = channel.getTotalViews();
        long videos = channel.getNumVideos();
        long subscribers = channel.getSubscribers();
        double averageViewsPerVideo = videos == 0 ? 0.0 : views / (double) videos;
        double viewsPerSubscriber = subscribers == 0 ? 0.0 : views / (double) subscribers;
        long channelAgeInMonths = channelAgeInMonths(channel);
        double uploadsPerMonth = channelAgeInMonths == 0 ? 0.0 : videos / (double) channelAgeInMonths;
        return new ChannelDTO(channel.getSavedToTheRepo(), channel.getYtId(), channel.getChannelName(), channel.getDescription(), channel.getChannelUrl(), channel.getCreationDate(), channel.getCountry(), channel.getLanguage(), channel.getThumbnails(), channel.getUploadedVidsPlaylistId(), channel.getTotalViews(), channel.getSubscribers(), channel.getNumVideos(), channel.getPrivacyStatus(), channel.isMadeForKids(),
                averageViewsPerVideo, viewsPerSubscriber, channelAgeInMonths, uploadsPerMonth);
    }

    private long channelAgeInMonths(YTChannel channel){
        LocalDate createdAt = channel.getCreationDate().atZone(ZoneOffset.UTC).toLocalDate();
        Period period = Period.between(createdAt, LocalDate.now(ZoneOffset.UTC));
        return period.getYears() * 12L + period.getMonths();
    }


    //handling more than one channel request
    public List<ChannelDTO> getManyChannels(List<String> channelInputs){
        List<String> ids = channelInputs.stream()
                .map(this::toChannelId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if(ids.isEmpty()){
            return List.of();
        }
        return fetchByIds(ids).stream()
                .map(this::toChannelDTO)
                .toList();
    }

    public String toChannelId(String data){
        if(data == null || data.isBlank()){
            return null;
        }
        String cleaned = data.trim();

        if(cleaned.startsWith("UC") && cleaned.length() == 24){
            return cleaned; //the entered data is an actual id
        }
        //else the provided data is a handle of a channel
        return channelRepository.findByChannelUrl(cleaned)
                .map(YTChannel::getYtId)
                .orElseGet(() -> {
                    YTChannel fetched = fetchAndSaveChannelByHandle(cleaned);
                    return fetched == null ? null : fetched.getYtId();
                });
    }

    private List<YTChannel> fetchByIds(List<String> ids){
        List<YTChannel> result = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for(String id : ids){
            channelRepository.findById(id).filter(this::isNew)
                    .ifPresentOrElse(result::add, () -> missing.add(id));
        }
        if(!missing.isEmpty()){
            for(ChannelResponse resp : youTubeClient.getMultipleChannelData(parts, String.join(",", missing))){
                YTChannel channel = toYTChannel(resp);
                result.add(channelRepository.save(channel));
                snapshotRepository.save(new YTChannelSnapshot(channel.getYtId(), channel.getTotalViews(), channel.getSubscribers(), channel.getNumVideos()));
            }
        }
        return result;
    }


    //see the historical snapshots of a given channel
    public List<YTChannelSnapshotDTO> getHistory(String channelInput){
        String channelId = toChannelId(channelInput);
        getOrCreateChannel(channelId);
        List<YTChannelSnapshot> result = snapshotRepository.findByChannelIdOrderByFetchedAtDesc(channelId);
        List<YTChannelSnapshotDTO> dto = new ArrayList<>();
        for(YTChannelSnapshot snapshot : result){
            dto.add(toSnapshotDTO(snapshot));
        }
        return dto;
    }


    private YTChannelSnapshotDTO toSnapshotDTO(YTChannelSnapshot snapshot){
        return new YTChannelSnapshotDTO(
                snapshot.getChannelId(), snapshot.getTotalViews(),
                snapshot.getSubscribers(), snapshot.getNumVideos(), snapshot.getFetchedAt()
        );
    }


    public String getUploadsPlaylistId(String channelInput){
        String id = toChannelId(channelInput);
        ChannelDTO channel = getChannelData(id);
        return channel.getUploadedVidsPlaylistId();
    }

    public void refreshSnapshot(String channelId){
        fetchAndSaveChannel(channelId);
    }

    public List<ChannelDTO> searchChannels(String searchQuery){
        SearchListResponse response = youTubeClient.search(searchQuery, "channel", null);
        List<String> ids = response.getItems().stream()
                .map(item -> item.getId().getChannelId())
                .toList();
        return getManyChannels(ids);
    }

}
