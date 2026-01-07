package com.college.notification.service;

import com.college.notification.dto.*;
import com.college.notification.entity.*;
import com.college.notification.mailing.MailService;
import com.college.notification.repository.*;
import com.college.notification.config.JwtUtils;
import com.college.notification.mailing.MailBodies;
import com.college.notification.mailing.MailSubjects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MailService mailService; // ✅ mail service

    /* ================= REGISTER ================= */
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // 1️⃣ Save USER
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsAdmin("TEACHER".equalsIgnoreCase(request.getRole()));

        User savedUser = userRepository.save(user);
        String uid = String.valueOf(savedUser.getId());

        // 2️⃣ Validate Department
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // 3️⃣ Role-based insert
        if ("TEACHER".equalsIgnoreCase(request.getRole())) {
            Teacher teacher = new Teacher();
            teacher.setUid(uid);
            teacher.setName(request.getName());
            teacher.setDeptId(department.getId());
            teacher.setDeptName(department.getName());
            teacher.setIsActive(true);
            teacherRepository.save(teacher);

        } else if ("STUDENT".equalsIgnoreCase(request.getRole())) {
            Student student = new Student();
            student.setUid(uid);
            student.setName(request.getName());
            student.setDeptId(department.getId());
            student.setDeptName(department.getName());
            student.setIsActive(true);
            studentRepository.save(student);

        } else {
            throw new RuntimeException("Invalid role (STUDENT / TEACHER)");
        }

        // ✅ SEND WELCOME EMAIL
        mailService.sendMail(
                user.getEmail(),
                MailSubjects.WELCOME,
                MailBodies.welcome(user.getName())
        );

        // 4️⃣ JWT
        String token = jwtUtils.generateToken(user.getEmail());
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getIsAdmin());
    }

    /* ================= LOGIN ================= */
    public String login(LoginRequest request) throws Exception {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new Exception("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception("Invalid password");
        }

        if (request.isTeacher() && !user.getIsAdmin()) {
            throw new Exception("User is not a teacher");
        }

        // ✅ SEND LOGIN ALERT MAIL
        mailService.sendMail(
                user.getEmail(),
                MailSubjects.LOGIN_ALERT,
                MailBodies.loginAlert()
        );

        return jwtUtils.generateToken(user.getEmail());
    }

    /* ================= RESET PASSWORD ================= */
    public void resetPassword(ResetPasswordRequest request) throws Exception {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new Exception("Email not registered"));

        // ✅ password mismatch check
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new Exception("Password mismatch");
        }

        // 🔐 reset password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // ✅ SEND PASSWORD RESET SUCCESS MAIL
        mailService.sendMail(
                user.getEmail(),
                MailSubjects.PASSWORD_RESET_SUCCESS,
                MailBodies.passwordResetSuccess()
        );
    }
}
