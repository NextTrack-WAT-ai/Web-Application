package com.nexttrack.spring_boot_app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Pair;

import com.nexttrack.spring_boot_app.Services.NextTrackAudioFeaturesService.TrackKey;
import com.nexttrack.spring_boot_app.model.NextTrackAudioFeatures;

import java.util.List;
import java.util.Optional;

public interface NextTrackAudioFeaturesRepo extends MongoRepository<NextTrackAudioFeatures, String> {

    @Query("{ name: '?0', artist: '?1' }")
    Optional<NextTrackAudioFeatures> findByNameAndArtist(String name, String artist);

    @Query("SELECT f FROM NextTrackAudioFeatures f WHERE (f.name,f.artist) IN :pairs")
    List<NextTrackAudioFeatures> findAllByNameAndArtistIn(@Param("pairs") List<TrackKey> keys);
}
