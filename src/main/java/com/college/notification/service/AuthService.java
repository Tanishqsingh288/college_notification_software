package com.college.notification.service;

import com.college.notification.model.User;
import com.college.notification.model.User.Role;
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
    private final PasswordEncoder passwordEncoder;

    // Use a static key for demo. In production, use ENV variable
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long TOKEN_EXPIRATION_MS = 60 * 60 * 1000; // 1 hour

    /**
     * Registers a new user with the given role (STUDENT or TEACHER).
     */
    public User register(String email, String password, String fullName, String roleString) {
        validateEmail(email);
        validatePassword(password);

        if (userRepository.existsByEmail(email)) {
            throw new AuthException("Email is already registered");
        }

        Role role = parseRole(roleString);

        User newUser = User.builder()
                .email(email.trim().toLowerCase())
                .password(passwordEncoder.encode(password))
                .fullName(fullName.trim())
                .role(role)
                .build();

        return userRepository.save(newUser);
    }

    /**
     * Logs in a user by validating email, password, and role.
     * Returns a JWT token on success.
     */
    public String login(String email, String password, String expectedRole) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new AuthException("Email and password must not be empty");
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new AuthException("Invalid email or password"));

        // Enforce login type: teacher login page must log in teachers only
        if (!user.getRole().name().equalsIgnoreCase(expectedRole)) {
            throw new AuthException("This account is not registered as a " + expectedRole);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }

        return generateToken(user);
    }

    /**
     * Decodes a JWT token and fetches the current authenticated user.
     */
    public User getCurrentUser(String token) {
        String email = extractEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));
    }

    /**
     * Allows the user to change their password securely.
     */
    public void changePassword(User user, String oldPassword, String newPassword) {
        validatePassword(newPassword);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AuthException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ----------------------------
    // 🧠 Helper Methods
    // ----------------------------

    public boolean isTeacher(User user) {
        return user.getRole() == Role.TEACHER;
    }

    public boolean isStudent(User user) {
        return user.getRole() == Role.STUDENT;
    }

    private Role parseRole(String roleString) {
        try {
            return Role.valueOf(roleString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AuthException("Invalid role. Must be STUDENT or TEACHER");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@") || email.length() < 5) {
            throw new AuthException("Invalid email address");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new AuthException("Password must be at least 6 characters long");
        }
    }

    // ----------------------------
    // 🔐 JWT Token Helpers
    // ----------------------------

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
