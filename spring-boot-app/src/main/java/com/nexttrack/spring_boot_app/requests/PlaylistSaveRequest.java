package com.nexttrack.spring_boot_app.requests;

import java.util.List;

import com.nexttrack.spring_boot_app.model.NextTrack;

public class PlaylistSaveRequest {
    private List<NextTrack> playlist;

    public List<NextTrack> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<NextTrack> playlist) {
        this.playlist = playlist;
    }
}
