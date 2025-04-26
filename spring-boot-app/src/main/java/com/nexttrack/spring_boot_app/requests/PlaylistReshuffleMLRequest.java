package com.nexttrack.spring_boot_app.requests;

import java.util.List;
import java.util.Map;

public class PlaylistReshuffleMLRequest {
    private String email;
    private List<Map<String, String>> playlist;

    public PlaylistReshuffleMLRequest(String email, List<Map<String, String>> playlist) {
        this.email = email;
        this.playlist = playlist;
    }

    public List<Map<String, String>> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<Map<String, String>> playlist) {
        this.playlist = playlist;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
