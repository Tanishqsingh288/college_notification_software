package com.college.notification.controller;

import com.college.notification.entity.Teacher;
import com.college.notification.entity.User;
import com.college.notification.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
}
