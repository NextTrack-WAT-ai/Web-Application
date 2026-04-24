package com.nexttrack.spring_boot_app.requests;

import java.util.List;

import com.nexttrack.spring_boot_app.model.NextTrack;

public class PlaylistReshuffleRequest {
    private List<NextTrack> tracks;

    public List<NextTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<NextTrack> tracks) {
        this.tracks = tracks;
    }
}
