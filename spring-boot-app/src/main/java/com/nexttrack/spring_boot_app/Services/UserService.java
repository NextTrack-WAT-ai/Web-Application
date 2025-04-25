package com.nexttrack.spring_boot_app.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
        List<String> emptyRemixes = new ArrayList<String>() {};
        final NextTrackUser newUser = new NextTrackUser(email, emptyRemixes);
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

    @Autowired
    private MongoTemplate mongoTemplate;

    public ResponseEntity<String> AddRemix(String email, String playlistId) {
        Optional<NextTrackUser> user = userRepo.findByEmail(email); 
        if (!user.isPresent()) {
            return new ResponseEntity<>("No user with this email exists", HttpStatus.BAD_REQUEST);
        }
        Query query = new Query(Criteria.where("email").is(email));

        Update update = new Update().addToSet("remixes", playlistId);

        mongoTemplate.updateFirst(query, update, NextTrackUser.class);
        
        return new ResponseEntity<>("Playlist added to remixes", HttpStatus.OK); 
    }
}
