package com.college.notification.repository;

import com.college.notification.model.Notice;
import com.college.notification.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByTeacher(User teacher);
}
