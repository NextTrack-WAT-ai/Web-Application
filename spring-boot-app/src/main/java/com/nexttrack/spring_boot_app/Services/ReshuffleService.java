package com.nexttrack.spring_boot_app.Services;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.nexttrack.spring_boot_app.model.ReshuffleResponse;

public class ReshuffleService {
    private final RestTemplate restTemplate;
    private final String apiUrl = "http://algorithm/predict";

    public ReshuffleService() {
        this.restTemplate = new RestTemplate();
    }

    public ReshuffleResponse reshuffle(List<String> trackIds) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        HttpEntity<List<String>> entity = new HttpEntity<>(trackIds, headers);

        try {
            ResponseEntity<ReshuffleResponse> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, ReshuffleResponse.class);

            return response.getBody(); 
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return null;
        }
    }
}
