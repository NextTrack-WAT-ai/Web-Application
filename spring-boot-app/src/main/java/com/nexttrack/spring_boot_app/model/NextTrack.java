package com.nexttrack.spring_boot_app.model;

import java.util.List;

public class NextTrack {
    private String trackId;
    private String name;
    private List<String> artists;
    private String trackUri;
    private String albumCoverUrl;
    private int durationMs; // duration in milliseconds
    private int trackIndex;

    public NextTrack() {
    }

    public NextTrack(String trackId, String name, List<String> artists, String trackUri, String albumCoverUrl,
            int durationMs) {
        this.trackId = trackId;
        this.name = name;
        this.artists = artists;
        this.trackUri = trackUri;
        this.albumCoverUrl = albumCoverUrl;
        this.durationMs = durationMs;
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

    public String getTrackUri() {
        return trackUri;
    }

    public void setTrackUri(String trackUri) {
        this.trackUri = trackUri;
    }

    public String getAlbumCoverUrl() {
        return albumCoverUrl;
    }

    public void setAlbumCoverUrl(String albumCoverUrl) {
        this.albumCoverUrl = albumCoverUrl;
    }

    public int getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(int durationMs) {
        this.durationMs = durationMs;
    }

    public int getTrackIndex() {
        return trackIndex;
    }

    public void setTrackIndex(int playlistIndex) {
        this.trackIndex = playlistIndex;
    }

    @Override
    public String toString() {
        return "ReshuffledTrack{" +
                "trackId='" + trackId + '\'' +
                ", name='" + name + '\'' +
                ", artists=" + artists +
                ", trackUri='" + trackUri + '\'' +
                ", albumCoverUrl='" + albumCoverUrl + '\'' +
                ", durationMs=" + durationMs +
                '}';
    }
}
