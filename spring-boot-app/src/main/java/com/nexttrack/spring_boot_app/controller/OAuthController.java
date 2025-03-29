package com.nexttrack.spring_boot_app.controller;

import java.io.IOException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexttrack.spring_boot_app.Services.SpotifyService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/api")
@RestController
public class OAuthController {

    private final SpotifyService spotifyService;

    public OAuthController(SpotifyService spotifyService){
        this.spotifyService = spotifyService;
    }
    @GetMapping("login")
    @ResponseBody
    public String spotifyLogin() {
        return spotifyService.getSpotifyLoginUrl();
    }
     
    @GetMapping("get-user-code")
    public String getMethodName(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        String accessToken = spotifyService.getTokens(userCode);

        if (accessToken != null) {
            response.sendRedirect("http://localhost:5173/landing/");
            return accessToken;
        } else {
            response.sendRedirect("http://localhost:5173/error/");
            return null;
        }
    }    
    
}
