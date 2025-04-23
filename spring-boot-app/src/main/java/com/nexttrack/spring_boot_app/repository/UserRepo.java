package com.nexttrack.spring_boot_app.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nexttrack.spring_boot_app.model.NextTrackUser;

public interface UserRepo extends MongoRepository<NextTrackUser, String> {
    @Query("{ email : '?0' }")
    Optional<NextTrackUser> findByEmail(String email); 

    
}