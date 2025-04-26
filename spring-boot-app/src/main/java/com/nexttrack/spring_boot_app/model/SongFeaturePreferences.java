package com.nexttrack.spring_boot_app.model;

public class SongFeaturePreferences {
    private double year = 1.0;
    private double durationMs = 1.0;
    private double danceability = 1.0;
    private double key = 1.0;
    private double loudness = 1.0;
    private double mode = 1.0;
    private double speechiness = 1.0;
    private double acousticness = 1.0;
    private double instrumentalness = 1.0;
    private double liveness = 1.0;
    private double valence = 1.0;
    private double tempo = 1.0;
    private double timeSignature = 1.0;

    public SongFeaturePreferences() {
        // Default constructor
    }

    // You can also add a constructor to initialize all values if you want
    public SongFeaturePreferences(double year, double durationMs, double danceability, double key, double loudness,
            double mode, double speechiness, double acousticness, double instrumentalness,
            double liveness, double valence, double tempo, double timeSignature) {
        this.year = year;
        this.durationMs = durationMs;
        this.danceability = danceability;
        this.key = key;
        this.loudness = loudness;
        this.mode = mode;
        this.speechiness = speechiness;
        this.acousticness = acousticness;
        this.instrumentalness = instrumentalness;
        this.liveness = liveness;
        this.valence = valence;
        this.tempo = tempo;
        this.timeSignature = timeSignature;
    }

    // Getters and Setters
    public double getYear() {
        return year;
    }

    public void setYear(double year) {
        this.year = year;
    }

    public double getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(double durationMs) {
        this.durationMs = durationMs;
    }

    public double getDanceability() {
        return danceability;
    }

    public void setDanceability(double danceability) {
        this.danceability = danceability;
    }

    public double getKey() {
        return key;
    }

    public void setKey(double key) {
        this.key = key;
    }

    public double getLoudness() {
        return loudness;
    }

    public void setLoudness(double loudness) {
        this.loudness = loudness;
    }

    public double getMode() {
        return mode;
    }

    public void setMode(double mode) {
        this.mode = mode;
    }

    public double getSpeechiness() {
        return speechiness;
    }

    public void setSpeechiness(double speechiness) {
        this.speechiness = speechiness;
    }

    public double getAcousticness() {
        return acousticness;
    }

    public void setAcousticness(double acousticness) {
        this.acousticness = acousticness;
    }

    public double getInstrumentalness() {
        return instrumentalness;
    }

    public void setInstrumentalness(double instrumentalness) {
        this.instrumentalness = instrumentalness;
    }

    public double getLiveness() {
        return liveness;
    }

    public void setLiveness(double liveness) {
        this.liveness = liveness;
    }

    public double getValence() {
        return valence;
    }

    public void setValence(double valence) {
        this.valence = valence;
    }

    public double getTempo() {
        return tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public double getTimeSignature() {
        return timeSignature;
    }

    public void setTimeSignature(double timeSignature) {
        this.timeSignature = timeSignature;
    }
}
