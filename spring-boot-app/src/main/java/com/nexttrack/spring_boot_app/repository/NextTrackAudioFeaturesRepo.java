package com.nexttrack.spring_boot_app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nexttrack.spring_boot_app.model.NextTrackAudioFeatures;

import java.util.Optional;

public interface NextTrackAudioFeaturesRepo extends MongoRepository<NextTrackAudioFeatures, String> {

    @Query("{ name: '?0', artist: '?1' }")
    Optional<NextTrackAudioFeatures> findByNameAndArtist(String name, String artist);
}
