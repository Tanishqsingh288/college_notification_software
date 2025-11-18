package com.college.notification.service;

import com.college.notification.dto.RegisterRequestDTO;
import com.college.notification.model.Department;
import com.college.notification.model.User;
import com.college.notification.repository.DepartmentRepository;
import com.college.notification.repository.UserRepository;
import com.college.notification.service.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long TOKEN_EXPIRATION_MS = 60 * 60 * 1000;

    // ------------------------
    // REGISTER
    // ------------------------
    public User register(RegisterRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new AuthException("Email already registered");
        }

        User.Role role;
        try {
            role = User.Role.valueOf(dto.getRole().trim().toUpperCase());
        } catch (RuntimeException e) {
            throw new AuthException("Invalid role: must be STUDENT or TEACHER");
        }

        Department dept = null;
        if (dto.getDepartmentId() != null) {
            dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new AuthException("Invalid department ID"));
        }

        User user = User.builder()
                .fullName(dto.getFullName().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(role)
                .department(dept)
                .build();

        return userRepository.save(user);
    }

    // ------------------------
    // LOGIN
    // ------------------------
    public String login(String email, String password, String expectedRole) {

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new AuthException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }

        if (!user.getRole().name().equalsIgnoreCase(expectedRole)) {
            throw new AuthException("This account is not a " + expectedRole);
        }

        return generateToken(user);
    }

    // ------------------------
    // GET CURRENT USER
    // ------------------------
    public User getCurrentUser(String token) {
        String email = extractEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));
    }

    // ------------------------
    // CHANGE PASSWORD
    // ------------------------
    public void changePassword(User user, String oldPass, String newPass) {

        if (!passwordEncoder.matches(oldPass, user.getPassword())) {
            throw new AuthException("Old password incorrect");
        }

        if (newPass.length() < 6) {
            throw new AuthException("New password must be 6+ chars");
        }

        user.setPassword(passwordEncoder.encode(newPass));
        userRepository.save(user);
    }

    // ------------------------
    // JWT METHODS
    // ------------------------
    private String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS))
                .signWith(SECRET_KEY)
                .compact();
    }

    private String extractEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();
            return claims.getSubject();

        } catch (Exception e) {
            throw new AuthException("Invalid or expired token");
        }
    }
}
