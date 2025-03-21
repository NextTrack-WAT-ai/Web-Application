package com.nexttrack.spring_boot_app.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.nexttrack.spring_boot_app.Services.UserService;
// import com.nexttrack.spring_boot_app.repository.UserRepo; uncomment when user repo is needed
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class UserController {
    @Autowired
    public UserService userService; 

    // @Autowired
    // private UserRepo userRepo; // establishes connection to user database 

    @PostMapping("user/register")
    public ResponseEntity<String> register(@RequestParam String username, @RequestParam String password) {
        return userService.RegisterUser(username, password); 
    }
    
}
