package com.nexttrack.spring_boot_app.Services;

import com.nexttrack.spring_boot_app.model.NextTrackAudioFeatures;
import com.nexttrack.spring_boot_app.repository.NextTrackAudioFeaturesRepo;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class NextTrackAudioFeaturesService {

    private final NextTrackAudioFeaturesRepo nextTrackAudioFeaturesRepo;

    public NextTrackAudioFeaturesService(NextTrackAudioFeaturesRepo nextTrackAudioFeaturesRepo) {
        this.nextTrackAudioFeaturesRepo = nextTrackAudioFeaturesRepo;
    }

    public NextTrackAudioFeatures findOrCreateTrack(String name, String artist) {

        var track = nextTrackAudioFeaturesRepo.findByNameAndArtist(name, artist);

        // TODO: Make call to audio feature extraction
        return track.orElse(new NextTrackAudioFeatures());

    }
}
