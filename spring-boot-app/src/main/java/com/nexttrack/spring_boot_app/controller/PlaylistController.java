package com.nexttrack.spring_boot_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexttrack.spring_boot_app.Services.NextTrackAudioFeaturesService;
import com.nexttrack.spring_boot_app.Services.ReshuffleService;
import com.nexttrack.spring_boot_app.Services.SpotifyService;
import com.nexttrack.spring_boot_app.Services.UserService;
import com.nexttrack.spring_boot_app.model.NextTrack;
import com.nexttrack.spring_boot_app.model.NextTrackAudioFeatures;
import com.nexttrack.spring_boot_app.model.PlaylistFeedback;
import com.nexttrack.spring_boot_app.requests.FeedbackRequest;
import com.nexttrack.spring_boot_app.requests.PlaylistReshuffleRequest;
import com.nexttrack.spring_boot_app.requests.PlaylistSaveRequest;

import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Ne;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/api")
@RestController
public class PlaylistController {

    private SpotifyService spotifyService;
    private ReshuffleService reshuffleService;
    private UserService userService;
    private NextTrackAudioFeaturesService nextTrackAudioFeaturesService;

    public PlaylistController(SpotifyService spotifyService, ReshuffleService reshuffleService,
            UserService userService,
            NextTrackAudioFeaturesService nextTrackAudioFeaturesService) {
        this.spotifyService = spotifyService;
        this.reshuffleService = reshuffleService;
        this.userService = userService;
        this.nextTrackAudioFeaturesService = nextTrackAudioFeaturesService;
    }

    @GetMapping("playlists/all")
    public PlaylistSimplified[] getAllPlaylists() {
        return spotifyService.getAllPlaylists();
    }

    @GetMapping("/remixes/{email}")
    public PlaylistSimplified[] getAllRemixes(@PathVariable String email) {
        var remixIds = userService.GetRemixes(email);
        var playlists = spotifyService.getAllPlaylists();
        List<PlaylistSimplified> filteredPlaylists = Arrays.stream(playlists)
                .filter(playlist -> remixIds.contains(playlist.getId()))
                .toList();

        return filteredPlaylists.toArray(new PlaylistSimplified[0]);

    }

    @GetMapping("playlist/{playlistId}")
    public List<NextTrack> getPlaylist(@PathVariable String playlistId) {
        PlaylistTrack[] items = spotifyService.getSongsFromPlaylist(playlistId);
        List<String> ids = Arrays.stream(items)
                .map(pt -> pt.getTrack().getId())
                .toList();

        Map<String, Track> fullMap = new HashMap<>();
        int batchSize = 50;

        for (int i = 0; i < ids.size(); i += batchSize) {
            int end = Math.min(i + batchSize, ids.size());
            List<String> batch = ids.subList(i, end);

            try {
                List<Track> fullTracks = spotifyService.getSeveralTracks(batch);

                for (Track track : fullTracks) {
                    fullMap.put(track.getId(), track);
                }

            } catch (Exception e) {
                System.err.println("Failed to fetch track batch from Spotify");
                e.printStackTrace();
            }
        }

        List<NextTrackAudioFeaturesService.TrackKey> keys = new ArrayList<>();
        List<NextTrack> dtoList = new ArrayList<>();

        for (PlaylistTrack pt : items) {
            Track full = fullMap.get(pt.getTrack().getId());
            if (full == null)
                continue;

            NextTrack nt = new NextTrack(
                    full.getId(),
                    full.getName(),
                    Arrays.stream(full.getArtists()).map(a -> a.getName()).toList(),
                    full.getUri(),
                    full.getAlbum() != null && full.getAlbum().getImages().length > 0
                            ? full.getAlbum().getImages()[0].getUrl()
                            : null,
                    full.getDurationMs());
            String artist = full.getArtists()[0].getName();
            keys.add(new NextTrackAudioFeaturesService.TrackKey(full.getName(), artist));
            dtoList.add(nt);
        }

        Map<NextTrackAudioFeaturesService.TrackKey, NextTrackAudioFeatures> features = nextTrackAudioFeaturesService
                .batchFindOrCreate(keys);

        for (int i = 0; i < dtoList.size(); i++) {
            NextTrack nt = dtoList.get(i);
            var key = keys.get(i);
            NextTrackAudioFeatures f = features.get(key);
            if (f != null) {
                nt.setDanceability(f.getDanceability());
                nt.setEnergy(f.getEnergy());
                nt.setKey(f.getKey());
                nt.setLoudness(f.getLoudness());
                nt.setMode(f.getMode());
                nt.setSpeechiness(f.getSpeechiness());
                nt.setAcousticness(f.getAcousticness());
                nt.setInstrumentalness(f.getInstrumentalness());
                nt.setLiveness(f.getLiveness());
                nt.setValence(f.getValence());
                nt.setTempo(f.getTempo());
            }
        }

        return dtoList;
    }

    @PostMapping("playlist/reshuffle")
    public List<NextTrack> reshuffle(@RequestBody PlaylistReshuffleRequest reshuffleRequest) {
        String email = reshuffleRequest.getEmail();
        List<NextTrack> tracks = reshuffleRequest.getTracks();
        Map<String, NextTrack> songInfoMap = new HashMap<>();

        generateSongInfoMap(tracks, songInfoMap);
        // Use the reshuffle service to process the list
        List<NextTrack> result = reshuffleService.reshuffle(email, songInfoMap);

        return result;
    }

    @PostMapping("playlist/save")
    public String reshuffle(@RequestBody PlaylistSaveRequest payload) {
        List<NextTrack> tracks = payload.getPlaylist();
        var createPlaylistResult = spotifyService.createPlaylist_Sync(payload.getUserId());
        String[] uris = new String[tracks.size()];
        for (int i = 0; i < tracks.size(); i++) {
            uris[i] = tracks.get(i).getTrackUri();
        }
        spotifyService.addItemsToPlaylist_Sync(createPlaylistResult.getPlaylistId(), uris);

        List<PlaylistFeedback> feedbackTracks = new ArrayList<>();
        for (NextTrack track : tracks) {
            feedbackTracks
                    .add(new PlaylistFeedback(track.getArtists().get(0), track.getName(), track.getTrackIndex()));
        }

        // update users weights
        // reshuffleService.saveReshuffleFeedback(payload.getEmail(), feedbackTracks);

        // save remix to user account
        userService.AddRemix(payload.getEmail(), createPlaylistResult.getPlaylistId());

        return createPlaylistResult.getSpotifyUrl();
    }

    @PostMapping("playlist/feedback")
    public List<Map<String, String>> feedback(@RequestBody FeedbackRequest payload) {
        return reshuffleService.saveReshuffleFeedback(payload.getEmail(), payload.getFeedbackTracks());
    }

    private void generateSongInfoMap(List<NextTrack> tracks, Map<String, NextTrack> songInfoMap) {
        for (NextTrack track : tracks) {
            String name = track.getName();
            List<String> artists = track.getArtists();
            if (artists != null && !artists.isEmpty()) {
                String key = name + " - " + artists.get(0); // Only use first artist for key

                songInfoMap.put(key, track);
            }
        }
    }

}
