package com.nexttrack.spring_boot_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.nexttrack.spring_boot_app.Services.SpotifyService;
import com.nexttrack.spring_boot_app.Services.UserService;
import com.nexttrack.spring_boot_app.model.NextTrackUser;

import se.michaelthelin.spotify.model_objects.specification.User;

// import com.nexttrack.spring_boot_app.repository.UserRepo; uncomment when user repo is needed
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/user")
public class UserController {

    public UserService userService;
    public SpotifyService spotifyService;

    public UserController(UserService userService, SpotifyService spotifyService) {
        this.userService = userService;
        this.spotifyService = spotifyService;

    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody NextTrackUser newUser) {
        return userService.RegisterUser(newUser);
    }

    @GetMapping("/login") // consider deprecating, but otherwise TODO add JWTs to this route
    public ResponseEntity<String> login(@RequestBody NextTrackUser attemptUser) {
        return userService.LoginUser(attemptUser);
    }

    @DeleteMapping("/logout") // delete method since we remove the JWT resource
    public ResponseEntity<String> logout() {
        return userService.LogoutUser();
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
