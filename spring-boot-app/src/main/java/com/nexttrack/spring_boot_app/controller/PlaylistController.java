package com.nexttrack.spring_boot_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexttrack.spring_boot_app.Services.ReshuffleService;
import com.nexttrack.spring_boot_app.Services.SpotifyService;
import com.nexttrack.spring_boot_app.model.ReshuffledTrack;

import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.IPlaylistItem;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;

@RequestMapping("/api")
@RestController
public class PlaylistController {   

    private SpotifyService spotifyService;
    private ReshuffleService reshuffleService;
        
    public PlaylistController(SpotifyService spotifyService, ReshuffleService reshuffleService){
        this.spotifyService = spotifyService;
        this.reshuffleService = reshuffleService;
    }
            
    @GetMapping("playlists/all")
    public PlaylistSimplified[] getAllPlaylists() {
        return spotifyService.getAllPlaylists();
    }
    
    @GetMapping("playlist")
    public PlaylistTrack[] getPlaylist(@RequestParam("id") String playlistId) {        
        return spotifyService.getSongsFromPlaylist(playlistId);
    }

    @GetMapping("playlist/reshuffle")
    public List<ReshuffledTrack> reshuffle(@RequestParam("id") String playlistId) {
    var playlistTracks = spotifyService.getSongsFromPlaylist(playlistId);
    var songInfoMap = new HashMap<String, String>();

    // Step 1: Collect all track IDs from playlist
    List<String> trackIds = new ArrayList<String>();

    for (int i = 0; i < playlistTracks.length; i++){
        trackIds.add(playlistTracks[i].getTrack().getId());
    }

    // Step 2: Process in batches of 50
    int batchSize = 50;
    for (int i = 0; i < trackIds.size(); i += batchSize) {
        int end = Math.min(i + batchSize, trackIds.size());
        List<String> batch = trackIds.subList(i, end);

        try {
            List<Track> fullTracks = spotifyService.getSeveralTracks(batch);

            for (Track track : fullTracks) {
                String trackName = track.getName();
                String artistNames = Arrays.stream(track.getArtists())
                                           .map(ArtistSimplified::getName)
                                           .collect(Collectors.joining(", "));

                String key = trackName + " - " + artistNames;
                songInfoMap.put(key, track.getId());
            }

        } catch (Exception e) {
            System.err.println("Failed to fetch track batch from Spotify");
            e.printStackTrace();
        }
    }

    var reshuffledSongsResult = reshuffleService.reshuffle(songInfoMap);
    List<ReshuffledTrack> reshuffledTracks = new ArrayList<ReshuffledTrack>();
    for (Map<String,String> map : reshuffledSongsResult) {
        var name = map.get("name");
        var artist = map.get("artist");
        var trackId = songInfoMap.get(name + " - " + artist);

        reshuffledTracks.add(new ReshuffledTrack(trackId, name, Arrays.asList(artist)));
        
    }
    return reshuffledTracks;
}

}
