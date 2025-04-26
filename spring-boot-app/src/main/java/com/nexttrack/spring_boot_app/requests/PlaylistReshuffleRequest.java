package com.nexttrack.spring_boot_app.requests;

import java.util.List;

import com.nexttrack.spring_boot_app.model.NextTrack;

public class PlaylistReshuffleRequest {
    private String email;
    private List<NextTrack> tracks;

    // Constructors, Getters, Setters
    public PlaylistReshuffleRequest(String email, List<NextTrack> tracks) {
        this.email = email;
        this.tracks = tracks;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<NextTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<NextTrack> tracks) {
        this.tracks = tracks;
    }
}
