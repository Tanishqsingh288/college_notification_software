package com.college.notification.dto;

public class LoginRequest {
    private String email;
    private String password;
    private boolean isTeacher; // true = Teacher login, false = Student login

    // getters & setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isTeacher() { return isTeacher; } // changed getter name
    public void setTeacher(boolean isTeacher) { this.isTeacher = isTeacher; }
}
