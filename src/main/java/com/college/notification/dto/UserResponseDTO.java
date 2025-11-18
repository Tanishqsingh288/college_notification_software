package com.college.notification.dto;

import com.college.notification.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private Long departmentId;

    public static UserResponseDTO fromEntity(User u) {
        return UserResponseDTO.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .role(u.getRole().name())
                .departmentId(u.getDepartment() != null ? u.getDepartment().getId() : null)
                .build();
    }
}
