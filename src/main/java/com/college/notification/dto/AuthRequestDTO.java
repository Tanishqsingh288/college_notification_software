package com.college.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    private String email;
    private String password;
    private String role; // STUDENT / TEACHER
}
