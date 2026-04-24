package com.nexttrack.spring_boot_app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexttrack.spring_boot_app.Services.NextTrackAudioFeaturesService;
import com.nexttrack.spring_boot_app.Services.ReshuffleService;
import com.nexttrack.spring_boot_app.Services.SpotifyService;
import com.nexttrack.spring_boot_app.Services.UserService;
import com.nexttrack.spring_boot_app.auth.AuthInterceptor;
import com.nexttrack.spring_boot_app.model.NextTrack;
import com.nexttrack.spring_boot_app.model.NextTrackAudioFeatures;
import com.nexttrack.spring_boot_app.model.PlaylistFeedback;
import com.nexttrack.spring_boot_app.requests.FeedbackRequest;
import com.nexttrack.spring_boot_app.requests.PlaylistReshuffleRequest;
import com.nexttrack.spring_boot_app.requests.PlaylistSaveRequest;
import com.nexttrack.spring_boot_app.responses.CreatePlaylistReponse;

import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestMapping("/api")
@RestController
public class PlaylistController {

    private static final Logger log = LoggerFactory.getLogger(PlaylistController.class);

    private final SpotifyService spotifyService;
    private final ReshuffleService reshuffleService;
    private final UserService userService;
    private final NextTrackAudioFeaturesService nextTrackAudioFeaturesService;

    public PlaylistController(SpotifyService spotifyService, ReshuffleService reshuffleService,
            UserService userService, NextTrackAudioFeaturesService nextTrackAudioFeaturesService) {
        this.spotifyService = spotifyService;
        this.reshuffleService = reshuffleService;
        this.userService = userService;
        this.nextTrackAudioFeaturesService = nextTrackAudioFeaturesService;
    }

    @GetMapping("playlists/all")
    public PlaylistSimplified[] getAllPlaylists(HttpServletRequest request) {
        String authKey = (String) request.getAttribute(AuthInterceptor.AUTH_KEY_ATTRIBUTE);
        return spotifyService.getAllPlaylists(authKey);
    }

    @GetMapping("/remixes")
    public ResponseEntity<PlaylistSimplified[]> getAllRemixes(HttpServletRequest request) {
        String authKey = (String) request.getAttribute(AuthInterceptor.AUTH_KEY_ATTRIBUTE);
        String email = spotifyService.getCurrentUsersEmail(authKey);
        if (email == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        List<String> remixIds = userService.GetRemixes(email);
        PlaylistSimplified[] playlists = spotifyService.getAllPlaylists(authKey);
        PlaylistSimplified[] filtered = Arrays.stream(playlists)
                .filter(p -> remixIds.contains(p.getId()))
                .toArray(PlaylistSimplified[]::new);

        return ResponseEntity.ok(filtered);
    }

    @GetMapping("playlist/{playlistId}")
    public ResponseEntity<List<NextTrack>> getPlaylist(@PathVariable String playlistId,
            HttpServletRequest request) {
        String authKey = (String) request.getAttribute(AuthInterceptor.AUTH_KEY_ATTRIBUTE);
        PlaylistTrack[] items = spotifyService.getSongsFromPlaylist(authKey, playlistId);

        List<String> ids = Arrays.stream(items)
                .map(pt -> pt.getTrack().getId())
                .toList();

        Map<String, Track> fullMap = new HashMap<>();
        int batchSize = 50;
        for (int i = 0; i < ids.size(); i += batchSize) {
            int end = Math.min(i + batchSize, ids.size());
            try {
                List<Track> batch = spotifyService.getSeveralTracks(authKey, ids.subList(i, end));
                for (Track track : batch) {
                    fullMap.put(track.getId(), track);
                }
            } catch (Exception e) {
                log.error("Failed to fetch track batch from Spotify: {}", e.getMessage());
            }
        }

        List<NextTrackAudioFeaturesService.TrackKey> keys = new ArrayList<>();
        List<NextTrack> dtoList = new ArrayList<>();

        for (PlaylistTrack pt : items) {
            Track full = fullMap.get(pt.getTrack().getId());
            if (full == null) continue;

            NextTrack nt = new NextTrack(
                    full.getId(),
                    full.getName(),
                    Arrays.stream(full.getArtists()).map(a -> a.getName()).toList(),
                    full.getUri(),
                    full.getAlbum() != null && full.getAlbum().getImages().length > 0
                            ? full.getAlbum().getImages()[0].getUrl()
                            : null,
                    full.getDurationMs());

            if (full.getArtists().length > 0) {
                keys.add(new NextTrackAudioFeaturesService.TrackKey(full.getName(), full.getArtists()[0].getName()));
            }
            dtoList.add(nt);
        }

        Map<NextTrackAudioFeaturesService.TrackKey, NextTrackAudioFeatures> features =
                nextTrackAudioFeaturesService.batchFindOrCreate(keys);

        for (int i = 0; i < dtoList.size() && i < keys.size(); i++) {
            NextTrackAudioFeatures f = features.get(keys.get(i));
            if (f != null) {
                NextTrack nt = dtoList.get(i);
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

        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("playlist/reshuffle")
    public ResponseEntity<List<NextTrack>> reshuffle(@RequestBody PlaylistReshuffleRequest reshuffleRequest,
            HttpServletRequest request) {
        String authKey = (String) request.getAttribute(AuthInterceptor.AUTH_KEY_ATTRIBUTE);
        String email = spotifyService.getCurrentUsersEmail(authKey);
        if (email == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        Map<String, NextTrack> songInfoMap = new HashMap<>();
        generateSongInfoMap(reshuffleRequest.getTracks(), songInfoMap);
        List<NextTrack> result = reshuffleService.reshuffle(email, songInfoMap);
        if (result == null) return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        return ResponseEntity.ok(result);
    }

    @PostMapping("playlist/save")
    public ResponseEntity<String> savePlaylist(@RequestBody PlaylistSaveRequest payload,
            HttpServletRequest request) {
        String authKey = (String) request.getAttribute(AuthInterceptor.AUTH_KEY_ATTRIBUTE);
        String email = spotifyService.getCurrentUsersEmail(authKey);
        String userId = spotifyService.getCurrentUsersId(authKey);
        if (email == null || userId == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        CreatePlaylistReponse created = spotifyService.createPlaylist(authKey, userId);
        if (created == null) return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();

        List<NextTrack> tracks = payload.getPlaylist();
        String[] uris = tracks.stream().map(NextTrack::getTrackUri).toArray(String[]::new);
        spotifyService.addItemsToPlaylist(authKey, created.getPlaylistId(), uris);

        userService.AddRemix(email, created.getPlaylistId());
        return ResponseEntity.ok(created.getSpotifyUrl());
    }

    @PostMapping("playlist/feedback")
    public ResponseEntity<List<Map<String, String>>> feedback(@RequestBody FeedbackRequest payload,
            HttpServletRequest request) {
        String authKey = (String) request.getAttribute(AuthInterceptor.AUTH_KEY_ATTRIBUTE);
        String email = spotifyService.getCurrentUsersEmail(authKey);
        if (email == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        List<Map<String, String>> result = reshuffleService.saveReshuffleFeedback(email, payload.getFeedbackTracks());
        if (result == null) return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        return ResponseEntity.ok(result);
    }

    private void generateSongInfoMap(List<NextTrack> tracks, Map<String, NextTrack> songInfoMap) {
        for (NextTrack track : tracks) {
            List<String> artists = track.getArtists();
            if (artists != null && !artists.isEmpty()) {
                songInfoMap.put(track.getName() + " - " + artists.get(0), track);
            }
        }
    }
}
