package com.college.notification.controller;

import com.college.notification.dto.*;
import com.college.notification.model.User;
import com.college.notification.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // -----------------------
    // REGISTER
    // -----------------------
    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody RegisterRequestDTO dto) {
        User user = authService.register(dto);
        return UserResponseDTO.fromEntity(user);
    }

    // -----------------------
    // LOGIN
    // -----------------------
    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody AuthRequestDTO dto) {
        String token = authService.login(dto.getEmail(), dto.getPassword(), dto.getRole());
        return new AuthResponseDTO(token);
    }

    // -----------------------
    // CURRENT USER
    // -----------------------
    @GetMapping("/me")
    public UserResponseDTO me(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        User user = authService.getCurrentUser(token);
        return UserResponseDTO.fromEntity(user);
    }

    // -----------------------
    // CHANGE PASSWORD
    // -----------------------
    @PostMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordDTO dto, HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        User user = authService.getCurrentUser(token);

        authService.changePassword(user, dto.getOldPassword(), dto.getNewPassword());
        return "Password updated successfully";
    }
}
