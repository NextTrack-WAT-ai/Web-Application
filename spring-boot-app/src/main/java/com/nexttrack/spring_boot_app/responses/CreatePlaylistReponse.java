package com.nexttrack.spring_boot_app.responses;

public class CreatePlaylistReponse {
    public String playlistId;
    public String spotifyUrl;

    public CreatePlaylistReponse(String playlistId, String spotifyUrl) {
        this.playlistId = playlistId;
        this.spotifyUrl = spotifyUrl;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }

}