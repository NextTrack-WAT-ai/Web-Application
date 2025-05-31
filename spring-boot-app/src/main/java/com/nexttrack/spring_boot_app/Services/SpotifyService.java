package com.nexttrack.spring_boot_app.Services;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import com.nexttrack.spring_boot_app.Keys;
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
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetUsersProfileRequest;

import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

@Service
public class SpotifyService {

    private String clientId = Keys.getKey("SPOTIFY_CLIENT_ID");
    private String clientSecret = Keys.getKey("SPOTIFY_CLIENT_SECRET");

    private static final URI redirectUri = URI.create("http://localhost:8080/api/get-user-code");

    private final SpotifyApi spotifyApi;

    public SpotifyService() {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }

    // Generate the Spotify login URL
    public String getSpotifyLoginUrl() {
        AuthorizationCodeUriRequest authCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("user-read-private, user-read-email, user-top-read, playlist-read-private, playlist-read-collaborative, playlist-modify-private, playlist-modify-public")
                .show_dialog(true)
                .build();

        URI uri = authCodeUriRequest.execute();
        return uri.toString();
    }

    // Handle the authorization callback to retrieve the access and refresh tokens
    public String getTokens(String code) {
        AuthorizationCodeRequest authCodeRequest = spotifyApi.authorizationCode(code).build();

        try {
            AuthorizationCodeCredentials authCodeCredentials = authCodeRequest.execute();
            spotifyApi.setAccessToken(authCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authCodeCredentials.getExpiresIn());
            return spotifyApi.getAccessToken();
        } catch (Exception exception) {
            System.out.println("Error: " + exception.getMessage());
            return null;
        }
    }

    public void refreshAccessToken() {
        try {
            AuthorizationCodeCredentials credentials = spotifyApi.authorizationCodeRefresh().build().execute();
            spotifyApi.setAccessToken(credentials.getAccessToken());
            if (credentials.getRefreshToken() != null) {
                spotifyApi.setRefreshToken(credentials.getRefreshToken());
            }
            System.out.println("Access token refreshed successfully.");
        } catch (Exception exception) {
            System.out.println("Error refreshing access token: " + exception.getMessage());
        }
    }

    public Artist[] getUserTopArtists() {
        refreshAccessToken();
        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists()
                .time_range("medium_term")
                .limit(10)
                .offset(5)
                .build();

        try {
            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
            return artistPaging.getItems();
        } catch (Exception exception) {
            System.out.println("Error: " + exception);
        }
        return new Artist[0];
    }

    public PlaylistSimplified[] getAllPlaylists() {
        refreshAccessToken();
        final GetListOfCurrentUsersPlaylistsRequest getListOfUsersPlaylistsRequest = spotifyApi
                .getListOfCurrentUsersPlaylists()
                .build();

        try {
            final Paging<PlaylistSimplified> playlistsPaging = getListOfUsersPlaylistsRequest.execute();
            return playlistsPaging.getItems();
        } catch (Exception exception) {
            System.out.println("Error: " + exception);
        }
        return new PlaylistSimplified[0];
    }

    public PlaylistSimplified[] getRemixes(List<String> remixes) {
        refreshAccessToken();
        final GetListOfCurrentUsersPlaylistsRequest getListOfUsersPlaylistsRequest = spotifyApi
                .getListOfCurrentUsersPlaylists()
                .build();

        try {
            final Paging<PlaylistSimplified> playlistsPaging = getListOfUsersPlaylistsRequest.execute();
            return playlistsPaging.getItems();
        } catch (Exception exception) {
            System.out.println("Error: " + exception);
        }
        return new PlaylistSimplified[0];
    }

    public PlaylistTrack[] getSongsFromPlaylist(String playlistId) {
        refreshAccessToken();
        final GetPlaylistsItemsRequest getPlaylistItemsRequest = spotifyApi.getPlaylistsItems(playlistId)
                .build();

        try {
            final Paging<PlaylistTrack> tracksPaging = getPlaylistItemsRequest.execute();
            return tracksPaging.getItems();
        } catch (Exception exception) {
            System.out.println("Error: " + exception);
        }
        return new PlaylistTrack[0];
    }

    public List<Track> getSeveralTracks(List<String> trackIds)
            throws IOException, SpotifyWebApiException, ParseException {
        refreshAccessToken();
        String[] idsArray = trackIds.toArray(new String[0]);
        return Arrays.asList(spotifyApi.getSeveralTracks(idsArray).build().execute());
    }

    public String getCurrentUsersId() {
        refreshAccessToken();
        try {
            GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi
                    .getCurrentUsersProfile()
                    .build();

            User user = getCurrentUsersProfileRequest.execute();
            return user.getId();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return null;
    }

    public String getCurrentUsersEmail() {
        refreshAccessToken();
        try {
            GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi
                    .getCurrentUsersProfile()
                    .build();
            User user = getCurrentUsersProfileRequest.execute();
            return user.getEmail();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public User getUsersProfile_Sync() {
        refreshAccessToken();
        String userId = getCurrentUsersId();
        GetUsersProfileRequest getUsersProfileRequest = spotifyApi.getUsersProfile(userId).build();
        try {
            final User user = getUsersProfileRequest.execute();

            return user;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public CreatePlaylistReponse createPlaylist_Sync(String userId) {
        refreshAccessToken();
        CreatePlaylistRequest createPlaylistRequest = spotifyApi
                .createPlaylist(userId, "NextTrack Reshuffled Playlist")
                .build();

        try {
            final Playlist playlist = createPlaylistRequest.execute();

            return new CreatePlaylistReponse(playlist.getId(), playlist.getExternalUrls().get("spotify"));
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public void addItemsToPlaylist_Sync(String playlistId, String[] uris) {
        refreshAccessToken();
        AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi
                .addItemsToPlaylist(playlistId, uris)
                .position(0)
                .build();
        try {
            addItemsToPlaylistRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
