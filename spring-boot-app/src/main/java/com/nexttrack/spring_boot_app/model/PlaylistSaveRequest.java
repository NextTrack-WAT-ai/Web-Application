package com.nexttrack.spring_boot_app.model;

import java.util.List;

public class PlaylistSaveRequest {
    private String userId;
    private List<NextTrack> playlist;
    private Boolean isLiked;

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

    public boolean getIsLiked(){
        return isLiked;
    }
}
