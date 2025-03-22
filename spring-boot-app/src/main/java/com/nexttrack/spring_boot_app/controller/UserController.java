package com.nexttrack.spring_boot_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.nexttrack.spring_boot_app.Services.UserService;
import com.nexttrack.spring_boot_app.model.User;

// import com.nexttrack.spring_boot_app.repository.UserRepo; uncomment when user repo is needed
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController("/user")
public class UserController {
    @Autowired
    public UserService userService; 

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User newUser) {
        return userService.RegisterUser(newUser); 
    }
    
    @GetMapping("/login") // consider deprecating, but otherwise TODO add JWTs to this route
    public ResponseEntity<String> login(@RequestBody User attemptUser) {
        return userService.LoginUser(attemptUser); 
    }
    
    @DeleteMapping("/logout") // delete method since we remove the JWT resource 
    public ResponseEntity<String> logout() {
        return userService.LogoutUser(); 
    }
}
