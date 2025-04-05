package com.nexttrack.spring_boot_app.model;

import java.util.List;

public class ReshuffleRequest {
    private List<String> playlist;

    // Constructors
    public ReshuffleRequest() {}
    public ReshuffleRequest(List<String> playlist) {
        this.playlist = playlist;
    }

    // Getter and Setter
    public List<String> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<String> playlist) {
        this.playlist = playlist;
    }

}
