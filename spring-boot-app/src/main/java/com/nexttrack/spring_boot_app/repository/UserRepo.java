package com.nexttrack.spring_boot_app.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nexttrack.spring_boot_app.model.NextTrackUser;


public interface UserRepo extends MongoRepository<NextTrackUser, Integer> { // just initializes the format of the mongo db
    // custom queries go here 
    @Query("{ 'username' : ?0 }") // custom method to get user based on unique username
    Optional<NextTrackUser> findByUsername(String username); 
}
