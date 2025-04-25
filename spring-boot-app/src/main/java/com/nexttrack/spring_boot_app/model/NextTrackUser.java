package com.nexttrack.spring_boot_app.model;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document // mongo stuff
public class NextTrackUser {
    private String email; 
    private List<String> remixes; 
    
    public NextTrackUser(String email, List<String> remixes) {
        this.email = email;  
        this.remixes = remixes; 
    }
    
    public String getEmail() {
        return email; 
    }

    public List<String> getRemixes() {
        return remixes; 
    }

    public void setEmail(String email) {
        this.email = email; 
    }

    public void addRemix(String playlistURL) {
        this.remixes.add(playlistURL); 
    }
}
