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

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestParam String email) {
        return userService.AddUser(email);
    }

    @GetMapping("/remixes")
    public List<String> getAllRemixes(@RequestParam String email) {
        return userService.GetRemixes(email); 
    }

    @CrossOrigin(origins = "http://localhost:5173") // for some reason we need to add this explicitly
    @PatchMapping("/remixes/add")
    public ResponseEntity<String> addRemix(@RequestParam String email, @RequestParam String playlistid) {
        return userService.AddRemix(email, playlistid); 
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
