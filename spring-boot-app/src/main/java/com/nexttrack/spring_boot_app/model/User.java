package com.nexttrack.spring_boot_app.model;
import org.springframework.data.mongodb.core.mapping.Document;

@Document // mongo stuff
public class User {
    private String username; 
    private String password; 
    
    // simple constructor, consider adding an empty constructor?
    public User(String username, String password) {
        this.username = username; 
        this.password = password; 
    }
    public String getUsername() {
        return username; 
    }

    public String getPassword() {
        return password; 
    }

    public void setUsername(String newUsername) {
        this.username = newUsername; 
    }

    public void setPassword(String newPassword) {
        this.password = newPassword; 
    }
}
