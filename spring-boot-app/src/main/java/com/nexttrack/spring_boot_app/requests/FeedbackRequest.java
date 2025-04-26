package com.nexttrack.spring_boot_app.requests;

import java.util.List;

import com.nexttrack.spring_boot_app.model.PlaylistFeedback;

public class FeedbackRequest {
    private String email;
    private List<PlaylistFeedback> feedbackTracks;

    // Constructors, Getters, Setters
    public FeedbackRequest(String email, List<PlaylistFeedback> feedbackTracks) {
        this.email = email;
        this.feedbackTracks = feedbackTracks;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PlaylistFeedback> getFeedbackTracks() {
        return feedbackTracks;
    }

    public void setFeedbackTracks(List<PlaylistFeedback> feedbackTracks) {
        this.feedbackTracks = feedbackTracks;
    }
}
