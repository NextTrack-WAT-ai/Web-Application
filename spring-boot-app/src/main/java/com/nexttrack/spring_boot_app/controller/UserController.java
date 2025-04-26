package com.nexttrack.spring_boot_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexttrack.spring_boot_app.Services.SpotifyService;
import com.nexttrack.spring_boot_app.Services.UserService;

import se.michaelthelin.spotify.model_objects.specification.User;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;
    private SpotifyService spotifyService;

    public UserController(UserService userService, SpotifyService spotifyService) {
        this.userService = userService;
        this.spotifyService = spotifyService;
    }

    @GetMapping("/email") // we call the endpoints in the order coded
    public String getEmail() {
        return spotifyService.getCurrentUsersEmail();
    }

    @PostMapping("/create/{email}")
    public ResponseEntity<String> createUser(@PathVariable String email) {
        return userService.AddUser(email);
    }

    @GetMapping("/remixes/{email}")
    public List<String> getAllRemixes(@PathVariable String email) {
        return userService.GetRemixes(email);
    }

    @GetMapping("/profile")
    public ResponseEntity<User> profile() {
        User user = spotifyService.getUsersProfile_Sync();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(user);
    }
}
