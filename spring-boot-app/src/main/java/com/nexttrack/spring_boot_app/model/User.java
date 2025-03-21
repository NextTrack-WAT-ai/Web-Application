package com.nexttrack.spring_boot_app.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document // mongo stuff
public class User {
    @Id // mongo stuff 
    // private Integer userId; // unnecessary, consider refactoring to use mongo object id
    private String username; 
    private String password; 
    
    // simple constructor, consider adding an empty constructor?
    public User( /*Integer userId, */String username, String password) {
        // this.userId = userId; 
        this.username = username; 
        this.password = password; 
    }

    // getter and setter methods for class 
    /*
    public Integer getId() {
        return userId; 
    }
    */
    public String getUsername() {
        return username; 
    }

    public String getPassword() {
        return password; 
    }

    public void setUsername(String newUsername) {
        this.username = newUsername; 
    }
}
