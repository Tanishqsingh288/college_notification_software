package com.college.notification.service;

import com.college.notification.entity.Department;
import com.college.notification.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    /**
     * Get all departments from the database
     *
     * @return List of Department objects
     */
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    /**
     * Optional: Add a new department
     */
    public Department addDepartment(String name) {
        Department department = new Department();
        department.setName(name);
        return departmentRepository.save(department);
    }


    public Department updateDepartment(Long deptId, String name) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        dept.setName(name);
        return departmentRepository.save(dept);
    }

    public Department deactivateDepartment(Long deptId) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        dept.setIsActive(false);
        return departmentRepository.save(dept);
    }

    public List<Department> listActiveDepartments() {
        return departmentRepository.findByIsActiveTrueOrderByNameAsc();
    }

    public List<Department> listInactiveDepartments() {
        return departmentRepository.findByIsActiveFalseOrderByNameAsc();
    }

    public List<Department> listRecentDepartments() {
        return departmentRepository.findAllByOrderByCreatedAtDesc();
    }
}
