package com.nexttrack.spring_boot_app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexttrack.spring_boot_app.Services.SpotifyService;
import com.nexttrack.spring_boot_app.Services.UserService;
import com.nexttrack.spring_boot_app.auth.AuthInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import se.michaelthelin.spotify.model_objects.specification.User;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final SpotifyService spotifyService;

    public UserController(UserService userService, SpotifyService spotifyService) {
        this.userService = userService;
        this.spotifyService = spotifyService;
    }

    @GetMapping("/email")
    public ResponseEntity<String> getEmail(HttpServletRequest request) {
        String authKey = (String) request.getAttribute(AuthInterceptor.AUTH_KEY_ATTRIBUTE);
        String email = spotifyService.getCurrentUsersEmail(authKey);
        if (email == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        return ResponseEntity.ok(email);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(HttpServletRequest request) {
        String authKey = (String) request.getAttribute(AuthInterceptor.AUTH_KEY_ATTRIBUTE);
        String email = spotifyService.getCurrentUsersEmail(authKey);
        if (email == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        return userService.AddUser(email);
    }

    @GetMapping("/profile")
    public ResponseEntity<User> profile(HttpServletRequest request) {
        String authKey = (String) request.getAttribute(AuthInterceptor.AUTH_KEY_ATTRIBUTE);
        User user = spotifyService.getUsersProfile(authKey);
        if (user == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        return ResponseEntity.ok(user);
    }
}
