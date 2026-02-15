package com.iats.app.model;

public class User {
    private Integer userId;
    private String fullName;
    private String email;
    private String username;
    private String role;
    private String passwordHash;

    public User(String fullName, String email, String username, String role, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
