package com.nexttrack.spring_boot_app.Services;

import com.nexttrack.spring_boot_app.Services.NextTrackAudioFeaturesService.AudioFeaturesResponse;
import com.nexttrack.spring_boot_app.Services.NextTrackAudioFeaturesService.TrackKey;
import com.nexttrack.spring_boot_app.configurations.ExternalServicesConfig;
import com.nexttrack.spring_boot_app.model.NextTrackAudioFeatures;
import com.nexttrack.spring_boot_app.repository.NextTrackAudioFeaturesRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NextTrackAudioFeaturesService {

    private static final Logger log = LoggerFactory.getLogger(NextTrackAudioFeaturesService.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final NextTrackAudioFeaturesRepo nextTrackAudioFeaturesRepo;
    private final WebClient webClient;

    public record TrackKey(String name, String artist) {}

    public NextTrackAudioFeaturesService(NextTrackAudioFeaturesRepo nextTrackAudioFeaturesRepo,
            ExternalServicesConfig config) {
        this.nextTrackAudioFeaturesRepo = nextTrackAudioFeaturesRepo;
        this.webClient = WebClient.builder()
                .baseUrl(config.getFeatureExtractionUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public NextTrackAudioFeatures findOrCreateTrack(String name, String artist) {
        return nextTrackAudioFeaturesRepo.findByNameAndArtist(name, artist)
                .orElseGet(() -> {
                    var requestBody = Map.of("artist", artist, "track_name", name);
                    AudioFeaturesResponse resp = webClient.post()
                            .uri("/extract_features")
                            .bodyValue(requestBody)
                            .retrieve()
                            .bodyToMono(AudioFeaturesResponse.class)
                            .timeout(TIMEOUT)
                            .block();

                    if (resp == null || resp.getFeatures() == null) return null;

                    AudioFeaturesResponse.Features f = resp.getFeatures();
                    NextTrackAudioFeatures entity = buildEntity(name, artist, f);
                    return nextTrackAudioFeaturesRepo.save(entity);
                });
    }

    public Map<TrackKey, NextTrackAudioFeatures> batchFindOrCreate(List<TrackKey> keys) {
        List<NextTrackAudioFeatures> existing = nextTrackAudioFeaturesRepo.findAllByNameAndArtistIn(keys);

        Map<TrackKey, NextTrackAudioFeatures> map = existing.stream()
                .collect(Collectors.toMap(
                        e -> new TrackKey(e.getName(), e.getArtist()),
                        Function.identity(),
                        (a, b) -> a));

        List<TrackKey> missing = keys.stream()
                .filter(k -> !map.containsKey(k))
                .toList();

        if (!missing.isEmpty()) {
            try {
                List<Map<String, String>> tracksList = missing.stream()
                        .map(k -> Map.of("track_name", k.name(), "artist", k.artist()))
                        .toList();

                List<AudioFeaturesResponse> responses = webClient.post()
                        .uri("/extract_features_batch")
                        .bodyValue(Map.of("tracks", tracksList))
                        .retrieve()
                        .bodyToFlux(AudioFeaturesResponse.class)
                        .timeout(TIMEOUT)
                        .collectList()
                        .block();

                if (responses == null) return map;

                List<NextTrackAudioFeatures> toSave = responses.stream()
                        .filter(r -> r.getTrack() != null && r.getFeatures() != null)
                        .map(r -> {
                            String[] parts = r.getTrack().split(" - ", 2);
                            if (parts.length < 2) {
                                log.warn("Unexpected track format from feature service: '{}'", r.getTrack());
                                return null;
                            }
                            return buildEntity(parts[0].trim(), parts[1].trim(), r.getFeatures());
                        })
                        .filter(e -> e != null)
                        .toList();

                nextTrackAudioFeaturesRepo.saveAll(toSave)
                        .forEach(e -> map.put(new TrackKey(e.getName(), e.getArtist()), e));

            } catch (Exception e) {
                log.error("Error fetching batch audio features: {}", e.getMessage());
            }
        }

        return map;
    }

    private NextTrackAudioFeatures buildEntity(String name, String artist, AudioFeaturesResponse.Features f) {
        NextTrackAudioFeatures e = new NextTrackAudioFeatures();
        e.setName(name);
        e.setArtist(artist);
        e.setAcousticness(f.getAcousticness());
        e.setDanceability(f.getDanceability());
        e.setEnergy(f.getEnergy());
        e.setInstrumentalness(f.getInstrumentalness());
        e.setKey(f.getKey());
        e.setLiveness(f.getLiveness());
        e.setLoudness(f.getLoudness());
        e.setSpeechiness(f.getSpeechiness());
        e.setTempo(f.getTempo());
        e.setValence(f.getValence());
        return e;
    }

    public static class AudioFeaturesResponse {
        private Features features;
        private String track;

        public static class Features {
            private double acousticness, danceability, energy, instrumentalness;
            private int key;
            private double liveness, loudness, speechiness, tempo, valence;

            public double getAcousticness() { return acousticness; }
            public void setAcousticness(double v) { this.acousticness = v; }
            public double getDanceability() { return danceability; }
            public void setDanceability(double v) { this.danceability = v; }
            public double getEnergy() { return energy; }
            public void setEnergy(double v) { this.energy = v; }
            public double getInstrumentalness() { return instrumentalness; }
            public void setInstrumentalness(double v) { this.instrumentalness = v; }
            public int getKey() { return key; }
            public void setKey(int v) { this.key = v; }
            public double getLiveness() { return liveness; }
            public void setLiveness(double v) { this.liveness = v; }
            public double getLoudness() { return loudness; }
            public void setLoudness(double v) { this.loudness = v; }
            public double getSpeechiness() { return speechiness; }
            public void setSpeechiness(double v) { this.speechiness = v; }
            public double getTempo() { return tempo; }
            public void setTempo(double v) { this.tempo = v; }
            public double getValence() { return valence; }
            public void setValence(double v) { this.valence = v; }
        }

        public Features getFeatures() { return features; }
        public void setFeatures(Features f) { this.features = f; }
        public String getTrack() { return track; }
        public void setTrack(String t) { this.track = t; }
    }
}
