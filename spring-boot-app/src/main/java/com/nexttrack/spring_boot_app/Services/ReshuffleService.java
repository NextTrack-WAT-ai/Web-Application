package com.nexttrack.spring_boot_app.Services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexttrack.spring_boot_app.model.NextTrack;
import com.nexttrack.spring_boot_app.model.PlaylistFeedback;
import com.nexttrack.spring_boot_app.requests.FeedbackRequest;
import com.nexttrack.spring_boot_app.requests.PlaylistReshuffleMLRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReshuffleService {

    private final WebClient webClient;

    public ReshuffleService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://recommendation-model-q48y.onrender.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public List<Map<String, String>> reshuffle(String email, Map<String, NextTrack> songInfoMap) {
        try {
            List<Map<String, String>> songDetailsList = new ArrayList<>();

            extractSongDetailsList(songInfoMap, songDetailsList);

            PlaylistReshuffleMLRequest request = new PlaylistReshuffleMLRequest(email, songDetailsList);

            System.out.println("Sending payload: " + new ObjectMapper().writeValueAsString(request));
            return webClient.post()
                    .uri("/shuffle")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, String>>>() {
                    })
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, String>> saveReshuffleFeedback(String email, List<PlaylistFeedback> feedbackTracks) {
        try {
            // Create the payload object
            FeedbackRequest feedbackRequest = new FeedbackRequest(email, feedbackTracks);

            // Log the payload being sent (for debugging purposes)
            System.out.println("Sending payload: " + new ObjectMapper().writeValueAsString(feedbackRequest));

            // Send the POST request with the payload
            return webClient.post()
                    .uri("/feedback")
                    .bodyValue(feedbackRequest) // Send the FeedbackRequest as the payload
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, String>>>() {
                    })
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void extractSongDetailsList(Map<String, NextTrack> songInfoMap, List<Map<String, String>> songDetailsList) {
        for (String key : songInfoMap.keySet()) {
            String[] parts = key.split(" - ", 2);

            if (parts.length == 2) {
                String songName = parts[0];
                String artistName = parts[1];

                Map<String, String> songMap = new HashMap<>();
                songMap.put("name", songName);
                songMap.put("artist", artistName);

                songDetailsList.add(songMap);
            }
        }
    }
}
