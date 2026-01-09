package com.college.notification.controller;

import com.college.notification.entity.Teacher;
import com.college.notification.entity.User;
import com.college.notification.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cns/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PutMapping("/{id}/admin-status")
    public User updateAdminStatus(
            @PathVariable Long id,
            @RequestParam boolean isAdmin
    ) {
        return teacherService.updateAdminStatus(id, isAdmin);
    }
    // GET /api/cns/users/admins
    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAllAdminUsers() {
        List<User> admins = teacherService.getAdminUsers();
        return ResponseEntity.ok(admins);
    }
}
