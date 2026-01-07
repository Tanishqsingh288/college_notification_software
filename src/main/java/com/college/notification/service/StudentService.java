package com.college.notification.service;

import com.college.notification.entity.Student;
import com.college.notification.entity.User;
import com.college.notification.repository.StudentRepository;
import com.college.notification.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Student> listAllStudents() {
        return studentRepository.findAllByOrderByNameAsc();
    }

    public List<Student> listActiveStudents() {
        return studentRepository.findByIsActiveTrueOrderByNameAsc();
    }

    public List<Student> listStudentsByDept(Long deptId) {
        return studentRepository.findByDeptIdOrderByNameAsc(deptId);
    }

    // ✅ Update email (User table, not Student)
    @Transactional
    public Student updateEmail(Long studentId, String email) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        User user = userRepository.findById(Long.valueOf(student.getUid()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(email);
        userRepository.save(user);

        return student;
    }

    // ✅ Update password (encoded, User table)
    @Transactional
    public Student updatePassword(Long studentId, String password) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        User user = userRepository.findById(Long.valueOf(student.getUid()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(password)); // 🔐 ENCODED
        userRepository.save(user);

        return student;
    }
}
