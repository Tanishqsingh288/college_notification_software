package com.college.notification.service;

import com.college.notification.model.Department;
import com.college.notification.model.Notice;
import com.college.notification.model.User;
import com.college.notification.repository.DepartmentRepository;
import com.college.notification.repository.NoticeRepository;
import com.college.notification.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

    // Fetch all departments
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    // Fetch all teachers
    public List<User> getAllTeachers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.TEACHER)
                .toList();
    }

    // Fetch all students
    public List<User> getAllStudents() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.STUDENT)
                .toList();
    }

    // Get summary counts
    public Map<String, Long> getSummary() {
        long totalUsers = userRepository.count();
        long totalTeachers = userRepository.findAll().stream().filter(u -> u.getRole() == User.Role.TEACHER).count();
        long totalStudents = userRepository.findAll().stream().filter(u -> u.getRole() == User.Role.STUDENT).count();
        long totalNotices = noticeRepository.count();

        return Map.of(
                "totalUsers", totalUsers,
                "totalTeachers", totalTeachers,
                "totalStudents", totalStudents,
                "totalNotices", totalNotices
        );
    }
}
