package com.nexttrack.spring_boot_app.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    String CLIENT_ID = "CLIENT_ID"; 
    private static final URI redirect_uri = SpotifyHttpManager.makeUri("http://localhost:8080/api/get-user-code");// we get this from the spotify login dashboard 
    private String code = ""; 

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder() // spotify itself provides us an object to do the heavy lifting
        .setClientId(Keys.CLIENT_ID.getKey()) // we will get this from a .env file, ignore this error for now 
        .setClientSecret(Keys.CLIENT_SECRET.getKey()) 
        .setRedirectUri(redirect_uri)
        .build(); 

    @GetMapping("login")
    @ResponseBody // allows us to send data as JSON or XML directly in the response of the endpoint 
    public String spotifyLogin(@RequestParam String param) {
        AuthorizationCodeUriRequest authCodeUriRequest = spotifyApi.authorizationCodeUri()
            .scope("user-read-private, user-read-email, user-top-read") // outlines the data we are retrieving
            .show_dialog(false)
            .build(); 
        return "test"; 
    }
    
}
