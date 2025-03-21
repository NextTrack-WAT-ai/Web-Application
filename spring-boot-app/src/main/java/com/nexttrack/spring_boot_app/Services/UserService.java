package com.nexttrack.spring_boot_app.Services;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nexttrack.spring_boot_app.model.User;
import com.nexttrack.spring_boot_app.repository.UserRepo;

@Service
public class UserService {
    @Autowired
    public UserRepo userRepo; // connects to user db repo

    public ResponseEntity<String> RegisterUser(String username, String password) {
        Optional<User> foundUser = userRepo.findByUsername(username); 
        if (!foundUser.isEmpty()) { 
            return new ResponseEntity<>("User with this username already exists!", HttpStatus.BAD_REQUEST); 
        }
        User newUser = new User(username, password);
        userRepo.save(newUser); // save to repository

        return new ResponseEntity<>("User registered succesfully!", HttpStatus.CREATED); 
    }
}
