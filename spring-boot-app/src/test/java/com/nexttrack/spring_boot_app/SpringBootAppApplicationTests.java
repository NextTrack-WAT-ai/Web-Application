package com.nexttrack.spring_boot_app;

import com.nexttrack.spring_boot_app.auth.TokenStore;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SpringBootAppApplicationTests {

    @Test
    void tokenStoreIssuesUniqueKeys() {
        TokenStore store = new TokenStore();
        String key1 = store.put("token-a1", "refresh-a1");
        String key2 = store.put("token-b1", "refresh-b1");
        assertNotEquals(key1, key2, "Each login should get a unique auth key");
    }

    @Test
    void tokenStoreReturnsCorrectTokensForKey() {
        TokenStore store = new TokenStore();
        String key = store.put("access123", "refresh456");
        Optional<TokenStore.TokenPair> pair = store.get(key);
        assertTrue(pair.isPresent());
        assertEquals("access123", pair.get().accessToken());
        assertEquals("refresh456", pair.get().refreshToken());
    }

    @Test
    void tokenStoreUpdatesAccessToken() {
        TokenStore store = new TokenStore();
        String key = store.put("old-access", "refresh");
        store.updateTokens(key, "new-access", null);
        Optional<TokenStore.TokenPair> pair = store.get(key);
        assertTrue(pair.isPresent());
        assertEquals("new-access", pair.get().accessToken());
        assertEquals("refresh", pair.get().refreshToken(), "Refresh token preserved when null is passed");
    }

    @Test
    void tokenStoreRemovesKey() {
        TokenStore store = new TokenStore();
        String key = store.put("access", "refresh");
        store.remove(key);
        assertTrue(store.get(key).isEmpty(), "Token should be gone after removal");
    }

    @Test
    void tokenStoreReturnEmptyForUnknownKey() {
        TokenStore store = new TokenStore();
        assertTrue(store.get("nonexistent").isEmpty());
        assertTrue(store.get(null).isEmpty());
    }
}
