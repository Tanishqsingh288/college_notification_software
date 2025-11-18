package com.college.notification.controller;

import com.college.notification.dto.NoticeResponseDTO;
import com.college.notification.model.Notice;
import com.college.notification.model.User;
import com.college.notification.service.AuthService;
import com.college.notification.service.StudentNoticeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class StudentNoticeController {

    private final StudentNoticeService studentNoticeService;
    private final AuthService authService;

    // Mark notice as viewed
    @PostMapping("/{id}/view")
    public String markAsViewed(@PathVariable Long id, HttpServletRequest req) {
        User student = authService.getCurrentUser(req.getHeader("Authorization"));
        studentNoticeService.markAsViewed(student, id);
        return "Notice marked as viewed";
    }

    // Get recent notices (last 5)
    @GetMapping("/recent")
    public List<NoticeResponseDTO> getRecentNotices() {
        return studentNoticeService.getRecentNotices().stream()
                .map(NoticeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Search notices by title/content
    @GetMapping("/search")
    public List<NoticeResponseDTO> searchNotices(@RequestParam String query) {
        return studentNoticeService.searchNotices(query).stream()
                .map(NoticeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
