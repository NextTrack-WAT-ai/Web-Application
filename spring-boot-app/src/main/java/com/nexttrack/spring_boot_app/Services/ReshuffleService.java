package com.nexttrack.spring_boot_app.Services;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexttrack.spring_boot_app.model.ReshuffleRequest;
import com.nexttrack.spring_boot_app.model.ReshuffleResponse;

import java.util.List;

@Service
public class ReshuffleService {

    private final WebClient webClient;

    public ReshuffleService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://recommendation-model-q48y.onrender.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // TODO add send artist name and song title as parameters
    public ReshuffleResponse reshuffle(List<String> trackIds) {
        try {
            var payload = new ReshuffleRequest(trackIds);
            System.out.println("Sending payload: " + new ObjectMapper().writeValueAsString(payload));
            return webClient.post()
                    .uri("/shuffle")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(ReshuffleResponse.class)
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
