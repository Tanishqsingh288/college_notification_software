package com.college.notification.dto;

public class VerifyRequest {
    private String email;
    private String password; // optional if you want to keep it simple

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
