package com.college.notification.repository;

import com.college.notification.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 1) All notices recent first
    List<Notice> findAllByOrderByCreatedAtDesc();

    // 2) Active notices recent first
    List<Notice> findByIsActiveTrueOrderByCreatedAtDesc();

    // 3) Non-active notices latest first
    List<Notice> findByIsActiveFalseOrderByCreatedAtDesc();

    // 4) Notices by department latest first
    List<Notice> findByDeptIdOrderByCreatedAtDesc(Long deptId);

    // 5) Notices by date range (createdAt between from and to)
    List<Notice> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant from, Instant to);

    // 6) Search notices by title (like search)
    List<Notice> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);

    List<Notice> findByKeywordContainingIgnoreCase(String keyword);

}
