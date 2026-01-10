package com.college.notification.repository;

import com.college.notification.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUid(String uid);

    List<Teacher> findByDeptId(Long deptId);
}
