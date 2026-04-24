package com.nexttrack.spring_boot_app.Services;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import com.nexttrack.spring_boot_app.auth.TokenStore;
import com.nexttrack.spring_boot_app.configurations.SpotifyConfig;
import com.nexttrack.spring_boot_app.responses.CreatePlaylistReponse;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SpotifyService {

    private static final Logger log = LoggerFactory.getLogger(SpotifyService.class);

    private final SpotifyConfig config;
    private final URI redirectUri;
    private final TokenStore tokenStore;

    public SpotifyService(SpotifyConfig config, TokenStore tokenStore) {
        this.config = config;
        this.redirectUri = URI.create(config.getApiUrl() + "/api/get-user-code");
        this.tokenStore = tokenStore;
    }

    private SpotifyApi buildBaseApi() {
        return new SpotifyApi.Builder()
                .setClientId(config.getClientId())
                .setClientSecret(config.getClientSecret())
                .setRedirectUri(redirectUri)
                .build();
    }

    /** Build a per-user SpotifyApi, refresh the access token, and update the store. */
    private SpotifyApi apiForKey(String authKey) {
        TokenStore.TokenPair pair = tokenStore.get(authKey)
                .orElseThrow(() -> new IllegalArgumentException("Unknown auth key"));

        SpotifyApi api = new SpotifyApi.Builder()
                .setClientId(config.getClientId())
                .setClientSecret(config.getClientSecret())
                .setRedirectUri(redirectUri)
                .setAccessToken(pair.accessToken())
                .setRefreshToken(pair.refreshToken())
                .build();

        try {
            AuthorizationCodeCredentials creds = api.authorizationCodeRefresh().build().execute();
            String newAccess = creds.getAccessToken();
            String newRefresh = creds.getRefreshToken();
            api.setAccessToken(newAccess);
            if (newRefresh != null) api.setRefreshToken(newRefresh);
            tokenStore.updateTokens(authKey, newAccess, newRefresh);
        } catch (Exception e) {
            log.warn("Token refresh failed, proceeding with existing token: {}", e.getMessage());
        }

        return api;
    }

    public String getSpotifyLoginUrl() {
        AuthorizationCodeUriRequest req = buildBaseApi().authorizationCodeUri()
                .scope("user-read-private, user-read-email, user-top-read, playlist-read-private, " +
                        "playlist-read-collaborative, playlist-modify-private, playlist-modify-public")
                .show_dialog(true)
                .build();
        return req.execute().toString();
    }

    /** Exchange the OAuth code for tokens. Returns the auth key to be sent to the client. */
    public String exchangeCodeForKey(String code) {
        SpotifyApi api = buildBaseApi();
        AuthorizationCodeRequest req = api.authorizationCode(code).build();
        try {
            AuthorizationCodeCredentials creds = req.execute();
            log.info("Tokens obtained, expires in {}s", creds.getExpiresIn());
            return tokenStore.put(creds.getAccessToken(), creds.getRefreshToken());
        } catch (Exception e) {
            log.error("Error exchanging OAuth code: {}", e.getMessage());
            return null;
        }
    }

    public Artist[] getUserTopArtists(String authKey) {
        try {
            Paging<Artist> paging = apiForKey(authKey).getUsersTopArtists()
                    .time_range("medium_term").limit(10).offset(5).build().execute();
            return paging.getItems();
        } catch (Exception e) {
            log.error("Error fetching top artists: {}", e.getMessage());
            return new Artist[0];
        }
    }

    public PlaylistSimplified[] getAllPlaylists(String authKey) {
        try {
            Paging<PlaylistSimplified> paging = apiForKey(authKey)
                    .getListOfCurrentUsersPlaylists().build().execute();
            return paging.getItems();
        } catch (Exception e) {
            log.error("Error fetching playlists: {}", e.getMessage());
            return new PlaylistSimplified[0];
        }
    }

    public PlaylistTrack[] getSongsFromPlaylist(String authKey, String playlistId) {
        try {
            Paging<PlaylistTrack> paging = apiForKey(authKey)
                    .getPlaylistsItems(playlistId).build().execute();
            return paging.getItems();
        } catch (Exception e) {
            log.error("Error fetching playlist tracks: {}", e.getMessage());
            return new PlaylistTrack[0];
        }
    }

    public List<Track> getSeveralTracks(String authKey, List<String> trackIds)
            throws IOException, SpotifyWebApiException, ParseException {
        String[] idsArray = trackIds.toArray(new String[0]);
        return Arrays.asList(apiForKey(authKey).getSeveralTracks(idsArray).build().execute());
    }

    public String getCurrentUsersId(String authKey) {
        try {
            User user = apiForKey(authKey).getCurrentUsersProfile().build().execute();
            return user.getId();
        } catch (Exception e) {
            log.error("Error fetching user ID: {}", e.getMessage());
            return null;
        }
    }

    public String getCurrentUsersEmail(String authKey) {
        try {
            User user = apiForKey(authKey).getCurrentUsersProfile().build().execute();
            return user.getEmail();
        } catch (Exception e) {
            log.error("Error fetching user email: {}", e.getMessage());
            return null;
        }
    }

    public User getUsersProfile(String authKey) {
        try {
            String userId = getCurrentUsersId(authKey);
            if (userId == null) return null;
            return apiForKey(authKey).getUsersProfile(userId).build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error fetching user profile: {}", e.getMessage());
            return null;
        }
    }

    public CreatePlaylistReponse createPlaylist(String authKey, String userId) {
        try {
            Playlist playlist = apiForKey(authKey)
                    .createPlaylist(userId, "NextTrack Reshuffled Playlist").build().execute();
            return new CreatePlaylistReponse(playlist.getId(), playlist.getExternalUrls().get("spotify"));
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error creating playlist: {}", e.getMessage());
            return null;
        }
    }

    public void addItemsToPlaylist(String authKey, String playlistId, String[] uris) {
        try {
            apiForKey(authKey).addItemsToPlaylist(playlistId, uris).position(0).build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error adding items to playlist: {}", e.getMessage());
        }
    }
}
