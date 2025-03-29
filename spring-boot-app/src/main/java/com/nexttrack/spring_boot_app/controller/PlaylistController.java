package com.nexttrack.spring_boot_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexttrack.spring_boot_app.Services.ReshuffleService;
import com.nexttrack.spring_boot_app.Services.SpotifyService;

import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

import java.util.ArrayList;
import java.util.List;

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
    public TrackSimplified[] getPlaylist(@RequestParam("id") String playlistId) {        
        return spotifyService.getSongsFromPlaylist();
    }

    @GetMapping("playlist/reshuffle")
    public List<String> reshuffle(@RequestParam("id") String playlistId) {        
        var songs = spotifyService.getSongsFromPlaylist();
        var songIds = new ArrayList<String>();

        for (TrackSimplified song : songs) {
            songIds.add(song.getId());
        }

        var reshuffledSongsResult = reshuffleService.reshuffle(songIds);
        return reshuffledSongsResult.getTrackIds();
    }

    
}
