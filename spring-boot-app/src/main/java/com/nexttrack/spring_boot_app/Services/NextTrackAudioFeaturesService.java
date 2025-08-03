package com.nexttrack.spring_boot_app.Services;

import com.nexttrack.spring_boot_app.Services.NextTrackAudioFeaturesService.AudioFeaturesResponse;
import com.nexttrack.spring_boot_app.Services.NextTrackAudioFeaturesService.TrackKey;
import com.nexttrack.spring_boot_app.model.NextTrackAudioFeatures;
import com.nexttrack.spring_boot_app.repository.NextTrackAudioFeaturesRepo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NextTrackAudioFeaturesService {

    private final NextTrackAudioFeaturesRepo nextTrackAudioFeaturesRepo;
    private final WebClient webClient;

    public record TrackKey(String name, String artist) {
    }

    public NextTrackAudioFeaturesService(NextTrackAudioFeaturesRepo nextTrackAudioFeaturesRepo) {
        this.nextTrackAudioFeaturesRepo = nextTrackAudioFeaturesRepo;
        this.webClient = WebClient.builder()
                .baseUrl("https://feature-extraction-service-production.up.railway.app/extract_features")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public NextTrackAudioFeatures findOrCreateTrack(String name, String artist) {
        return nextTrackAudioFeaturesRepo.findByNameAndArtist(name, artist)
                .orElseGet(() -> {
                    var requestBody = Map.of(
                            "artist", artist,
                            "track_name", name);

                    AudioFeaturesResponse resp = webClient.post()
                            .bodyValue(requestBody)
                            .retrieve()
                            .bodyToMono(AudioFeaturesResponse.class)
                            .block();

                    AudioFeaturesResponse.Features f = resp.getFeatures();

                    NextTrackAudioFeatures entity = new NextTrackAudioFeatures();
                    entity.setName(name);
                    entity.setArtist(artist);
                    entity.setAcousticness(f.getAcousticness());
                    entity.setDanceability(f.getDanceability());
                    entity.setEnergy(f.getEnergy());
                    entity.setInstrumentalness(f.getInstrumentalness());
                    entity.setKey(f.getKey());
                    entity.setLiveness(f.getLiveness());
                    entity.setLoudness(f.getLoudness());
                    entity.setSpeechiness(f.getSpeechiness());
                    entity.setTempo(f.getTempo());
                    entity.setValence(f.getValence());
                    return nextTrackAudioFeaturesRepo.save(entity);
                });
    }

    public Map<TrackKey, NextTrackAudioFeatures> batchFindOrCreate(List<TrackKey> keys) {
        List<NextTrackAudioFeatures> existing = nextTrackAudioFeaturesRepo
                .findAllByNameAndArtistIn(keys);

        // TODO: optimize retreival of existing tracks
        Map<TrackKey, NextTrackAudioFeatures> map = existing.stream()
                .collect(Collectors.toMap(
                        e -> new TrackKey(e.getName(), e.getArtist()),
                        Function.identity(),
                        (existingValue, duplicateValue) -> existingValue));
        List<TrackKey> missing = keys.stream()
                .filter(k -> !map.containsKey(k))
                .toList();

        if (!missing.isEmpty()) {
            try {
                List<Map<String, String>> tracksList = missing.stream()
                        .map(k -> Map.of("track_name", k.name, "artist", k.artist))
                        .toList();

                Map<String, Object> payload = Map.of("tracks", tracksList);
                // System.out.println("Sending payload: " + new
                // ObjectMapper().writeValueAsString(payload));

                List<AudioFeaturesResponse> responses = webClient.post()
                        .uri("https://feature-extraction-service-production.up.railway.app/extract_features_batch")
                        .bodyValue(payload)
                        .retrieve()
                        .bodyToFlux(AudioFeaturesResponse.class)
                        .collectList()
                        .block();

                List<NextTrackAudioFeatures> toSave = responses.stream().map(r -> {
                    var f = r.getFeatures();
                    NextTrackAudioFeatures e = new NextTrackAudioFeatures();
                    e.setName(r.getTrack().split(" - ")[0]); // track format is "name - artist"
                    e.setArtist(r.getTrack().split(" - ")[1]);
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
                }).toList();

                List<NextTrackAudioFeatures> saved = nextTrackAudioFeaturesRepo.saveAll(toSave);

                saved.forEach(e -> map.put(new TrackKey(e.getName(), e.getArtist()), e));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return map;
    }

    public static class AudioFeaturesResponse {
        private Features features;
        private String track;

        public static class Features {
            private double acousticness;
            private double danceability;
            private double energy;
            private double instrumentalness;
            private int key;
            private double liveness;
            private double loudness;
            private double speechiness;
            private double tempo;
            private double valence;

            public double getAcousticness() {
                return acousticness;
            }

            public void setAcousticness(double acousticness) {
                this.acousticness = acousticness;
            }

            public double getDanceability() {
                return danceability;
            }

            public void setDanceability(double danceability) {
                this.danceability = danceability;
            }

            public double getEnergy() {
                return energy;
            }

            public void setEnergy(double energy) {
                this.energy = energy;
            }

            public double getInstrumentalness() {
                return instrumentalness;
            }

            public void setInstrumentalness(double instrumentalness) {
                this.instrumentalness = instrumentalness;
            }

            public int getKey() {
                return key;
            }

            public void setKey(int key) {
                this.key = key;
            }

            public double getLiveness() {
                return liveness;
            }

            public void setLiveness(double liveness) {
                this.liveness = liveness;
            }

            public double getLoudness() {
                return loudness;
            }

            public void setLoudness(double loudness) {
                this.loudness = loudness;
            }

            public double getSpeechiness() {
                return speechiness;
            }

            public void setSpeechiness(double speechiness) {
                this.speechiness = speechiness;
            }

            public double getTempo() {
                return tempo;
            }

            public void setTempo(double tempo) {
                this.tempo = tempo;
            }

            public double getValence() {
                return valence;
            }

            public void setValence(double valence) {
                this.valence = valence;
            }
        }

        public Features getFeatures() {
            return features;
        }

        public void setFeatures(Features features) {
            this.features = features;
        }

        public String getTrack() {
            return track;
        }

        public void setTrack(String track) {
            this.track = track;
        }
    }

}
