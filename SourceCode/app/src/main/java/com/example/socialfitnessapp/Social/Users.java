package com.example.socialfitnessapp.Social;

public class Users {
    private String name;
    private String bio;
    private String surname;
    private String username;
    private String profilePicture;

    public Users() {
    }

    public Users(String name, String bio, String surname, String username, String profilePicture) {
        this.name = name;
        this.bio = bio;
        this.surname = surname;
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }

    public void setBio(String bio) { this.bio = bio; }

    public String getSurname() { return surname; }

    public void setSurname(String surname) { this.surname = surname; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getProfilePicture() { return profilePicture; }

    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
}
