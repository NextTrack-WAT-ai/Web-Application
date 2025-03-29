package com.nexttrack.spring_boot_app.controller;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexttrack.spring_boot_app.Keys;

import jakarta.servlet.http.HttpServletResponse;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

// check out the tutorial here: https://bret-gibson.medium.com/spotify-api-authentication-with-spring-boot-and-react-23a68f4e79bb
// this guy is saving my butt

// all methods for the spotify api java package are here: https://github.com/spotify-web-api-java/spotify-web-api-java
@RequestMapping("/api")
@RestController
public class OAuthController {
    public static String getRandomString(int length) {
        String randomStr = UUID.randomUUID().toString();
        while(randomStr.length() < length) {
            randomStr += UUID.randomUUID().toString();
        }
        return randomStr.substring(0, length);
    }

    private static final URI redirect_uri = SpotifyHttpManager.makeUri("http://localhost:8080/api/get-user-code");// we get this from the spotify login dashboard 
    private String code = ""; 

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder() // spotify itself provides us an object to do the heavy lifting
        .setClientId(Keys.getKey("CLIENT_KEY")) // we will get this from a .env file, ignore this error for now 
        .setClientSecret(Keys.getKey("CLIENT_SECRET")) 
        .setRedirectUri(redirect_uri)
        .build(); 

    @GetMapping("login")
    @ResponseBody // allows us to send data as JSON or XML directly in the response of the endpoint 
    public String spotifyLogin() {
        AuthorizationCodeUriRequest authCodeUriRequest = spotifyApi.authorizationCodeUri()
            .scope("user-read-private, user-read-email, user-top-read, playlist-read-private, playlist-read-collaborative, playlist-modify-private, playlist-modify-public") 
            //above outlines the data we retrieve from the user's spotify account
            .show_dialog(true)
            .build(); 
        final URI uri = authCodeUriRequest.execute(); 
        return uri.toString();
    }
    
    // please touch this as little as possible, I am not 100% sure what this does
    // gets the user token from the oauth redirect, I think
    @GetMapping("get-user-code")
    public String getMethodName(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        code = userCode; // global from the variable in this class, not to be confused with from the requestparam
        AuthorizationCodeRequest authCodeRequest = spotifyApi.authorizationCode(code).build(); 

        try {
            final AuthorizationCodeCredentials authCodeCredentials = authCodeRequest.execute(); 
 
            // we extract the tokens themselves, I think.
            spotifyApi.setAccessToken(authCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authCodeCredentials.getExpiresIn());
        } catch (IOException| SpotifyWebApiException | org.apache.hc.core5.http.ParseException exception) {
            System.out.println("Error: " + exception.getMessage());
        }

        // we redirect the user to the landing page 
        response.sendRedirect("http://localhost:5173/landing/"); // this is a placeholder for a url on the frontend
        return spotifyApi.getAccessToken(); // we return the token 
    }
    
    // this takes code abstraction to a new level...
    @GetMapping("user-top-artists")
    public Artist[] getUserTopArtists() { // artist is apparently a unique object
        // THIS IS A SPECIFIC REQUEST????
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
    
    // SHOULD get all user playlists 
    @GetMapping("all-user-playlists")
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
    
    // more docs: https://spotify-web-api-java.github.io/spotify-web-api-java/apidocs/se/michaelthelin/spotify/model_objects/specification/Track.html
    // i am beginning to lose my sanity
    @GetMapping("all-songs-from-playlist")
    public TrackSimplified[] getSongsFromPlaylist() {
        
        return new TrackSimplified[0]; 
    }
    
}
