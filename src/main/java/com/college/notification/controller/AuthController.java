package com.college.notification.controller;

import com.college.notification.dto.*;
import com.college.notification.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cns/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);

            String role = request.isTeacher() ? "TEACHER" : "STUDENT";
            String email = request.getEmail();

            LoginResponse response = new LoginResponse(token, email, role);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(null); // For errors, you could make a separate ErrorResponse DTO
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/resetpassword")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        try {
            authService.resetPassword(request);
            return ResponseEntity.ok(
                    new ResetPasswordResponse("Password reset successful")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
