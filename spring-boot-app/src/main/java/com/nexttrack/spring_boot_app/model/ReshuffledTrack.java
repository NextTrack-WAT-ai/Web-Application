package com.nexttrack.spring_boot_app.model;

import java.util.List;

public class ReshuffledTrack {
    private String trackId;
    private String name;
    private List<String> artists;

    public ReshuffledTrack() {
    }

    public ReshuffledTrack(String trackId, String name, List<String> artists) {
        this.trackId = trackId;
        this.name = name;
        this.artists = artists;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArtists() {
        return artists;
    }

    public void setArtists(List<String> artists) {
        this.artists = artists;
    }

    @Override
    public String toString() {
        return "ReshuffledTrack{" +
                "trackId='" + trackId + '\'' +
                ", name='" + name + '\'' +
                ", artists=" + artists +
                '}';
    }
}
