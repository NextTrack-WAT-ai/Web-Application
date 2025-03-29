package com.nexttrack.spring_boot_app.Services;

import java.net.URI;
import com.nexttrack.spring_boot_app.Keys;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;

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
    
    public Artist[] getUserTopArtists() {
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
        final GetListOfCurrentUsersPlaylistsRequest getListOfUsersPlaylistsRequest = spotifyApi.getListOfCurrentUsersPlaylists()
            .build(); 
        
        try {
            final Paging<PlaylistSimplified> playlistsPaging = getListOfUsersPlaylistsRequest.execute(); 
            return playlistsPaging.getItems(); 
        } catch (Exception exception) {
            System.out.println("Error: " + exception);
        }
        return new PlaylistSimplified[0]; 
    }
        
    public TrackSimplified[] getSongsFromPlaylist() {
        
        return new TrackSimplified[0]; 
    }    
}
