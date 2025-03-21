package com.nexttrack.spring_boot_app.Services;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nexttrack.spring_boot_app.model.User;
import com.nexttrack.spring_boot_app.repository.UserRepo;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo; // connects to user db repo

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12); // we construct Bcrypt object with strength of 12

    public ResponseEntity<String> RegisterUser(User newUser) {
        Optional<User> foundUser = userRepo.findByUsername(newUser.getUsername()); 
        if (!foundUser.isEmpty()) { 
            return new ResponseEntity<>("User with this username already exists!", HttpStatus.BAD_REQUEST); 
        }

        // TODO add password length and strength testing here

        // we first hash the password using bcrypt
        newUser.setPassword(encoder.encode(newUser.getPassword()));
        
        userRepo.save(newUser); // save to repository

        return new ResponseEntity<>("User registered succesfully!", HttpStatus.CREATED); 
    }

    public ResponseEntity<String> LoginUser(User attemptUser) {
        Optional<User> foundUser = userRepo.findByUsername(attemptUser.getUsername()); 
        if (foundUser.isEmpty()) {
            return new ResponseEntity<>("No user with this username exists", HttpStatus.BAD_REQUEST); 
        }

        String hashed_attempt = encoder.encode(attemptUser.getPassword()); 
        if (!hashed_attempt.equals(foundUser.get().getPassword())) {
            return new ResponseEntity<>("Wrong username or password", HttpStatus.BAD_REQUEST); 
        }

        // TODO give the user a JWT for API route validation >W<

        return new ResponseEntity<>("Logged in succesfully!", HttpStatus.ACCEPTED); // consider using HtppStatus.CREATED 
    }

    public ResponseEntity<String> LogoutUser() { // user JWT should be passed through request headers
        // TODO remove JWT from user 
        return new ResponseEntity<>("Logged out succesfully!", HttpStatus.OK); 
    }
}
