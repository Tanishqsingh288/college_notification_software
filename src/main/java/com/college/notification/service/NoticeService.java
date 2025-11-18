package com.college.notification.service;

import com.college.notification.dto.NoticeRequestDTO;
import com.college.notification.model.Notice;
import com.college.notification.model.User;
import com.college.notification.repository.NoticeRepository;
import com.college.notification.service.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // ------------------------
    // CREATE NOTICE (Teacher Only)
    // ------------------------
    public Notice createNotice(User teacher, NoticeRequestDTO dto) {
        if (teacher.getRole() != User.Role.TEACHER) {
            throw new AuthException("Only teachers can create notices");
        }

        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .body(dto.getContent())
                .teacher(teacher)
                .build();

        return noticeRepository.save(notice);
    }

    // ------------------------
    // UPDATE NOTICE
    // ------------------------
    public Notice updateNotice(User teacher, Long noticeId, NoticeRequestDTO dto) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new AuthException("Notice not found"));

        if (!notice.getTeacher().getId().equals(teacher.getId())) {
            throw new AuthException("You can only update your own notices");
        }

        notice.setTitle(dto.getTitle());
        notice.setBody(dto.getContent());

        return noticeRepository.save(notice);
    }

    // ------------------------
    // DELETE NOTICE
    // ------------------------
    public void deleteNotice(User teacher, Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new AuthException("Notice not found"));

        if (!notice.getTeacher().getId().equals(teacher.getId())) {
            throw new AuthException("You can only delete your own notices");
        }

        noticeRepository.delete(notice);
    }

    // ------------------------
    // LIST ALL NOTICES
    // ------------------------
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    // ------------------------
    // GET NOTICE BY ID
    // ------------------------
    public Notice getNoticeById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new AuthException("Notice not found"));
    }
}
