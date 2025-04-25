package com.nexttrack.spring_boot_app.model;

public class PlaylistFeedback {
    private String artist;
    private String name;
    private int position;

    public PlaylistFeedback(String artist, String name, int position) {
        this.artist = artist;
        this.name = name;
        this.position = position;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
