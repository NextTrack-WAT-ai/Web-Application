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

    @GetMapping("playlist/{playlistId}")
    public List<NextTrack> getPlaylist(@PathVariable String playlistId) {
        PlaylistTrack[] playlistTracks = spotifyService.getSongsFromPlaylist(playlistId);

        // Extract track IDs
        List<String> trackIds = Arrays.stream(playlistTracks)
                .map(pt -> pt.getTrack().getId())
                .collect(Collectors.toList());

        // Create a map of full track info keyed by track ID
        Map<String, Track> fullTrackMap = new HashMap<>();
        int batchSize = 50;

        for (int i = 0; i < trackIds.size(); i += batchSize) {
            int end = Math.min(i + batchSize, trackIds.size());
            List<String> batch = trackIds.subList(i, end);

            try {
                List<Track> fullTracks = spotifyService.getSeveralTracks(batch);

                for (Track track : fullTracks) {
                    fullTrackMap.put(track.getId(), track);
                }

            } catch (Exception e) {
                System.err.println("Failed to fetch track batch from Spotify");
                e.printStackTrace();
            }
        }

        // Construct NextTrack list
        return Arrays.stream(playlistTracks).map(pt -> {
            String id = pt.getTrack().getId();
            Track full = fullTrackMap.get(id);

            if (full == null)
                return null;

            String name = full.getName();

            List<String> artistNames = Arrays.stream(full.getArtists())
                    .map(ArtistSimplified::getName)
                    .collect(Collectors.toList());

            String uri = full.getUri();
            int durationMs = full.getDurationMs();

            String albumCover = null;
            if (full.getAlbum() != null && full.getAlbum().getImages().length > 0) {
                albumCover = full.getAlbum().getImages()[0].getUrl();
            }
            var track = new NextTrack(id, name, artistNames, uri, albumCover, durationMs);
            NextTrackAudioFeatures audioFeatures = nextTrackAudioFeaturesService.findOrCreateTrack(name,
                    artistNames.get(0));

            track.setDanceability(audioFeatures.getDanceability());
            track.setEnergy(audioFeatures.getEnergy());
            track.setKey(audioFeatures.getKey());
            track.setLoudness(audioFeatures.getLoudness());
            track.setMode(audioFeatures.getMode());
            track.setSpeechiness(audioFeatures.getSpeechiness());
            track.setAcousticness(audioFeatures.getAcousticness());
            track.setInstrumentalness(audioFeatures.getInstrumentalness());
            track.setLiveness(audioFeatures.getLiveness());
            track.setValence(audioFeatures.getValence());
            track.setTempo(audioFeatures.getTempo());

            return track;

        }).filter(Objects::nonNull).collect(Collectors.toList());
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
