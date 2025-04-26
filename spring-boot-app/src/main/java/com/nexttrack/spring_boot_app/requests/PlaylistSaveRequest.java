package com.nexttrack.spring_boot_app.requests;

import java.util.List;

import com.nexttrack.spring_boot_app.model.NextTrack;

public class PlaylistSaveRequest {
    private String userId;
    private String email;
    private List<NextTrack> playlist;

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
