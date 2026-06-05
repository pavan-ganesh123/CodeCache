package com.example.demo.dto;

public class ProfileResponse {

    private Long id;
    private String userName;
    private String email;
    private String profilePicture;
    
    public ProfileResponse(Long id, String userName, String email, String profilePicture) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }
}