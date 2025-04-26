package com.nexttrack.spring_boot_app.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document // mongo stuff
public class NextTrackUser {
    private String email;
    private List<String> remixes;
    private SongFeaturePreferences songFeaturePreferences;

    public NextTrackUser(String email, List<String> remixes, SongFeaturePreferences songFeaturePreferences) {
        this.email = email;
        this.remixes = remixes;
        this.songFeaturePreferences = songFeaturePreferences;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRemixes() {
        return remixes;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addRemix(String playlistURL) {
        this.remixes.add(playlistURL);
    }

    public SongFeaturePreferences getSongFeaturePreferences() {
        return songFeaturePreferences;
    }

    public void setSongFeaturePreferences(SongFeaturePreferences songFeaturePreferences) {
        this.songFeaturePreferences = songFeaturePreferences;
    }
}
