package com.nexttrack.spring_boot_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexttrack.spring_boot_app.Services.SpotifyService;

import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import org.springframework.web.bind.annotation.GetMapping;

@RequestMapping("/api")
@RestController
public class PlaylistController {   

    private SpotifyService spotifyService;
    
        public PlaylistController(SpotifyService spotifyService){
            this.spotifyService = spotifyService;
        }
            
    @GetMapping("playlists/all")
    public PlaylistSimplified[] getAllPlaylists() {
        return spotifyService.getAllPlaylists();
    }
    
    @GetMapping("playlist")
    public TrackSimplified[] getPlaylist(@RequestParam("id") String playlistId) {        
        return spotifyService.getSongsFromPlaylist();
    }

    
}
