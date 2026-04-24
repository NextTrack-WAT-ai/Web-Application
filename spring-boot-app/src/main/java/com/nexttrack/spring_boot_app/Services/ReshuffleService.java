package com.nexttrack.spring_boot_app.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.nexttrack.spring_boot_app.configurations.ExternalServicesConfig;
import com.nexttrack.spring_boot_app.model.NextTrack;
import com.nexttrack.spring_boot_app.model.PlaylistFeedback;
import com.nexttrack.spring_boot_app.requests.FeedbackRequest;
import com.nexttrack.spring_boot_app.requests.PlaylistReshuffleMLRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReshuffleService {

    private static final Logger log = LoggerFactory.getLogger(ReshuffleService.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(60);

    private final WebClient webClient;

    public ReshuffleService(ExternalServicesConfig config) {
        this.webClient = WebClient.builder()
                .baseUrl(config.getReshuffleUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public List<NextTrack> reshuffle(String email, Map<String, NextTrack> songInfoMap) {
        try {
            List<NextTrack> songs = new ArrayList<>(songInfoMap.values());
            PlaylistReshuffleMLRequest request = new PlaylistReshuffleMLRequest(email, songs);
            return webClient.post()
                    .uri("/shuffle")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<NextTrack>>() {})
                    .timeout(TIMEOUT)
                    .block();
        } catch (Exception e) {
            log.error("Error calling reshuffle service: {}", e.getMessage());
            return null;
        }
    }

    public List<Map<String, String>> saveReshuffleFeedback(String email, List<PlaylistFeedback> feedbackTracks) {
        try {
            FeedbackRequest feedbackRequest = new FeedbackRequest(email, feedbackTracks);
            return webClient.post()
                    .uri("/feedback")
                    .bodyValue(feedbackRequest)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, String>>>() {})
                    .timeout(TIMEOUT)
                    .block();
        } catch (Exception e) {
            log.error("Error saving reshuffle feedback: {}", e.getMessage());
            return null;
        }
    }
}
