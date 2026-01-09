package com.college.notification.controller;

import com.college.notification.entity.Student;
import com.college.notification.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cns/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public List<Student> listAll(
            @RequestParam(value = "search", required = false) String search
    ) {
        return studentService.searchStudents(search);
    }

    @GetMapping("/active")
    public List<Student> listActive() { return studentService.listActiveStudents(); }

    @GetMapping("/department/{deptId}")
    public List<Student> listByDept(@PathVariable Long deptId) { return studentService.listStudentsByDept(deptId); }

    @PutMapping("/{id}/email")
    public Student updateEmail(@PathVariable Long id, @RequestParam String email) { return studentService.updateEmail(id, email); }

    @PutMapping("/{id}/password")
    public Student updatePassword(@PathVariable Long id, @RequestParam String password) { return studentService.updatePassword(id, password); }
}
