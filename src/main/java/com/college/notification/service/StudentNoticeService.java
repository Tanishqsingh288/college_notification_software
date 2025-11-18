package com.college.notification.service;

import com.college.notification.model.Notice;
import com.college.notification.model.StudentNoticeView;
import com.college.notification.model.User;
import com.college.notification.repository.StudentNoticeViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentNoticeService {

    private final StudentNoticeViewRepository viewRepository;
    private final NoticeService noticeService;

    // Mark notice as viewed by student
    public void markAsViewed(User student, Long noticeId) {
        if (student.getRole() != User.Role.STUDENT) return;

        Notice notice = noticeService.getNoticeById(noticeId);

        viewRepository.findByStudentAndNotice(student, notice)
                .orElseGet(() -> viewRepository.save(StudentNoticeView.builder()
                        .student(student)
                        .notice(notice)
                        .build()));
    }

    // Get recent notices (latest 5)
    public List<Notice> getRecentNotices() {
        return noticeService.getAllNotices().stream()
                .sorted((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());
    }

    // Search notices by title or content
    public List<Notice> searchNotices(String query) {
        return noticeService.getAllNotices().stream()
                .filter(n -> n.getTitle().toLowerCase().contains(query.toLowerCase())
                        || n.getBody().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}
