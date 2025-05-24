package com.nexttrack.spring_boot_app.requests;

import java.util.List;
import java.util.Map;

import com.nexttrack.spring_boot_app.model.NextTrack;

public class PlaylistReshuffleMLRequest {
    private String email;
    private List<NextTrack> playlist;

    public PlaylistReshuffleMLRequest(String email, List<NextTrack> playlist) {
        this.email = email;
        this.playlist = playlist;
    }

    public List<NextTrack> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<NextTrack> playlist) {
        this.playlist = playlist;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
