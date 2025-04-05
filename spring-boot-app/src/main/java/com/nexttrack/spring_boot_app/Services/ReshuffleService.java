package com.nexttrack.spring_boot_app.Services;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.nexttrack.spring_boot_app.model.ReshuffleResponse;

import java.util.List;

@Service
public class ReshuffleService {

    private final WebClient webClient;

    public ReshuffleService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://algorithm")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public ReshuffleResponse reshuffle(List<String> trackIds) {
        try {
            return webClient.post()
                    .uri("/predict")
                    .bodyValue(trackIds)
                    .retrieve()
                    .bodyToMono(ReshuffleResponse.class)
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
