package com.college.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private String fullName;
    private String email;
    private String password;
    private Long departmentId;  // nullable for teacher
    private String role; // STUDENT or TEACHER
}
