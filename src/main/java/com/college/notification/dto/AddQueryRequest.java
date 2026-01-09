package com.college.notification.dto;
public class AddQueryRequest {

    private String title;
    private String description;
    private String sentByEmail;

    // -------- Getters & Setters --------

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSentByEmail() {
        return sentByEmail;
    }

    public void setSentByEmail(String sentByEmail) {
        this.sentByEmail = sentByEmail;
    }
}
