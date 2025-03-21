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
        if (!foundUser.isEmpty()) { // make sure that usernames are unique before registration
            return new ResponseEntity<>("User with this username already exists!", HttpStatus.BAD_REQUEST); 
        }
        // Integer id = 1; // unncessary, mongo generates object ids automatically 
        User newUser = new User(/*id, */ username, password); // we create a new user with the user model 
        userRepo.save(newUser); // save to db 

        // OPTIONAL consider adding error handling for internal server errors?

        return new ResponseEntity<>("User registered succesfully!", HttpStatus.CREATED); 
    }
}
