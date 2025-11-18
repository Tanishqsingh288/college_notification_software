package com.college.notification.repository;

import com.college.notification.model.Notice;
import com.college.notification.model.StudentNoticeView;
import com.college.notification.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentNoticeViewRepository extends JpaRepository<StudentNoticeView, Long> {
    Optional<StudentNoticeView> findByStudentAndNotice(User student, Notice notice);
    List<StudentNoticeView> findAllByStudent(User student);
}
