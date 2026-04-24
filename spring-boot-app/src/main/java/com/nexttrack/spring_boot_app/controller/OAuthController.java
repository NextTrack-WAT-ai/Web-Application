package com.nexttrack.spring_boot_app.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nexttrack.spring_boot_app.Services.SpotifyService;

import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("/api")
@RestController
public class OAuthController {

    private final SpotifyService spotifyService;

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    public OAuthController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("login")
    @ResponseBody
    public String spotifyLogin() {
        return spotifyService.getSpotifyLoginUrl();
    }

    @GetMapping("get-user-code")
    public void handleOAuthCallback(@RequestParam("code") String userCode, HttpServletResponse response)
            throws IOException {
        String authKey = spotifyService.exchangeCodeForKey(userCode);

        if (authKey != null) {
            response.sendRedirect(frontendUrl + "/landing?auth=" + authKey);
        } else {
            response.sendRedirect(frontendUrl + "/error/");
        }
    }
}
