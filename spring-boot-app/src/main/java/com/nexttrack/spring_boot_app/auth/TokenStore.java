package com.nexttrack.spring_boot_app.auth;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class TokenStore {

    public record TokenPair(String accessToken, String refreshToken) {}

    private final ConcurrentHashMap<String, TokenPair> store = new ConcurrentHashMap<>();

    public String put(String accessToken, String refreshToken) {
        String key = UUID.randomUUID().toString();
        store.put(key, new TokenPair(accessToken, refreshToken));
        return key;
    }

    public Optional<TokenPair> get(String key) {
        return Optional.ofNullable(key).map(store::get);
    }

    public void updateTokens(String key, String newAccessToken, String newRefreshToken) {
        store.computeIfPresent(key, (k, v) ->
                new TokenPair(newAccessToken, newRefreshToken != null ? newRefreshToken : v.refreshToken()));
    }

    public void remove(String key) {
        if (key != null) store.remove(key);
    }
}
