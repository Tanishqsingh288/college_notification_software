package com.college.notification.controller;

import com.college.notification.model.Department;
import com.college.notification.model.User;
import com.college.notification.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // Fetch all departments
    @GetMapping("/departments")
    public List<Department> getDepartments() {
        return adminService.getAllDepartments();
    }

    // List all teachers
    @GetMapping("/users/teachers")
    public List<User> getTeachers() {
        return adminService.getAllTeachers();
    }

    // List all students
    @GetMapping("/users/students")
    public List<User> getStudents() {
        return adminService.getAllStudents();
    }

    // Get system summary
    @GetMapping("/stats/summary")
    public Map<String, Long> getSummary() {
        return adminService.getSummary();
    }
}
