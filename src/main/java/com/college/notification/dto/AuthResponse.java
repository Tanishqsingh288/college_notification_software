package com.college.notification.dto;

public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private boolean isAdmin;

    public AuthResponse(String token, String name, String email, boolean isAdmin) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    // getters
    public String getToken() { return token; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public boolean getIsAdmin() { return isAdmin; }
}
