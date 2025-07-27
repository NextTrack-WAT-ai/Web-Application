package com.nexttrack.spring_boot_app.Services;

import com.nexttrack.spring_boot_app.model.NextTrackAudioFeatures;
import com.nexttrack.spring_boot_app.repository.NextTrackAudioFeaturesRepo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;

@Service
public class NextTrackAudioFeaturesService {

    private final NextTrackAudioFeaturesRepo nextTrackAudioFeaturesRepo;
    private final WebClient webClient;

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
