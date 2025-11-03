package com.college.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // ❌ Disable CSRF for APIs
                .authorizeHttpRequests(auth -> auth
                        // 👇 Allow these endpoints without authentication (e.g. login/register)
                        .requestMatchers("/auth/**").permitAll()
                        // 👇 All other endpoints need authentication
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {}); // for now, use basic auth (we'll switch to JWT later)

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
