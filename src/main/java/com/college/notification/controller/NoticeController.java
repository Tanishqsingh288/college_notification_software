package com.college.notification.controller;

import com.college.notification.dto.NoticeRequestDTO;
import com.college.notification.dto.NoticeResponseDTO;
import com.college.notification.model.Notice;
import com.college.notification.model.User;
import com.college.notification.service.AuthService;
import com.college.notification.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final AuthService authService;

    // ------------------------
    // LIST ALL NOTICES
    // ------------------------
    @GetMapping
    public List<NoticeResponseDTO> getAllNotices() {
        return noticeService.getAllNotices().stream()
                .map(NoticeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ------------------------
    // GET NOTICE BY ID
    // ------------------------
    @GetMapping("/{id}")
    public NoticeResponseDTO getNoticeById(@PathVariable Long id) {
        return NoticeResponseDTO.fromEntity(noticeService.getNoticeById(id));
    }

    // ------------------------
    // CREATE NOTICE (Teacher Only)
    // ------------------------
    @PostMapping
    public NoticeResponseDTO createNotice(@RequestBody NoticeRequestDTO dto, HttpServletRequest req) {
        User teacher = authService.getCurrentUser(req.getHeader("Authorization"));
        Notice notice = noticeService.createNotice(teacher, dto);
        return NoticeResponseDTO.fromEntity(notice);
    }

    // ------------------------
    // UPDATE NOTICE
    // ------------------------
    @PutMapping("/{id}")
    public NoticeResponseDTO updateNotice(@PathVariable Long id, @RequestBody NoticeRequestDTO dto,
                                          HttpServletRequest req) {
        User teacher = authService.getCurrentUser(req.getHeader("Authorization"));
        Notice notice = noticeService.updateNotice(teacher, id, dto);
        return NoticeResponseDTO.fromEntity(notice);
    }

    // ------------------------
    // DELETE NOTICE
    // ------------------------
    @DeleteMapping("/{id}")
    public String deleteNotice(@PathVariable Long id, HttpServletRequest req) {
        User teacher = authService.getCurrentUser(req.getHeader("Authorization"));
        noticeService.deleteNotice(teacher, id);
        return "Notice deleted successfully";
    }
}
