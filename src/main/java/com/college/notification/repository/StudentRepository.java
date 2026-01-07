package com.college.notification.repository;

import com.college.notification.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAllByOrderByNameAsc();
    List<Student> findByIsActiveTrueOrderByNameAsc();
    List<Student> findByDeptIdOrderByNameAsc(Long deptId);
}
