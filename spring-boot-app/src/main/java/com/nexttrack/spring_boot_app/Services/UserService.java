package com.nexttrack.spring_boot_app.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nexttrack.spring_boot_app.model.NextTrackUser;
import com.nexttrack.spring_boot_app.repository.UserRepo;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo; 

    public Boolean UserExists(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    public ResponseEntity<String> AddUser(String email) {
        if (UserExists(email)) {
            return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT); 
        }
        final NextTrackUser newUser = new NextTrackUser(email, null);
        userRepo.save(newUser);
        return new ResponseEntity<>("User created succesfully", HttpStatus.CREATED); 
    }

    public List<String> GetRemixes(String email) { // ensure that we call this after AddUser is finished
        Optional<NextTrackUser> User = userRepo.findByEmail(email);
        if (!User.isPresent()) {
            return new ArrayList<String>(); // we return an empty list, just better error handling
        } else {
            return User.get().getRemixes(); 
        }
    }
}
