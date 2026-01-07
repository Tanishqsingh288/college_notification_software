package com.college.notification.controller;

import com.college.notification.entity.Department;
import com.college.notification.repository.DepartmentRepository;
import com.college.notification.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/cns/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public List<String> getAllDepartments() {
        return departmentService.getAllDepartments()
                .stream()
                .map(Department::getName)
                .collect(Collectors.toList());
    }

    @PostMapping
    public Department add(@RequestParam String name) { return departmentService.addDepartment(name); }

    @PutMapping("/{id}")
    public Department update(@PathVariable Long id, @RequestParam String name) { return departmentService.updateDepartment(id, name); }

    @DeleteMapping("/{id}")
    public Department deactivate(@PathVariable Long id) { return departmentService.deactivateDepartment(id); }

    @GetMapping("/active")
    public List<Department> active() { return departmentService.listActiveDepartments(); }

    @GetMapping("/inactive")
    public List<Department> inactive() { return departmentService.listInactiveDepartments(); }

    @GetMapping("/recent")
    public List<Department> recent() { return departmentService.listRecentDepartments(); }
}
