package com.nexttrack.spring_boot_app.model;

import java.util.List;

public class ReshuffleResponse {

    private List<String> trackIds;
    
    public ReshuffleResponse() {
    }
    
    public List<String> getTrackIds() {
        return trackIds;
    }

    public void setTrackIds(List<String> trackIds) {
        this.trackIds = trackIds;
    } 
}
