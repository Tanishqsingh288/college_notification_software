package com.college.notification.controller;

import com.college.notification.dto.FileUploadResponse;
import com.college.notification.dto.NoticeUploadRequest;
import com.college.notification.entity.Notice;
import com.college.notification.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cns/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 1️⃣ List ALL notices
    // GET /api/cns/notices
    @GetMapping
    public ResponseEntity<List<Notice>> getAllNotices() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }

    // 2️⃣ List ACTIVE notices
    // GET /api/cns/notices/active
    @GetMapping("/active")
    public ResponseEntity<List<Notice>> getActiveNotices() {
        return ResponseEntity.ok(noticeService.getActiveNotices());
    }

    // 3️⃣ List INACTIVE notices
    // GET /api/cns/notices/inactive
    @GetMapping("/inactive")
    public ResponseEntity<List<Notice>> getInactiveNotices() {
        return ResponseEntity.ok(noticeService.getNonActiveNotices());
    }

    // 4️⃣ List notices by DEPARTMENT
    // GET /api/cns/notices/department/{deptId}
    @GetMapping("/department/{deptId}")
    public ResponseEntity<List<Notice>> getNoticesByDepartment(@PathVariable Long deptId) {
        return ResponseEntity.ok(noticeService.getNoticesByDept(deptId));
    }

    // 5️⃣ List notices by DATE RANGE
    // GET /api/cns/notices/daterange?from=...&to=...
    @GetMapping("/daterange")
    public ResponseEntity<List<Notice>> getNoticesByDateRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        return ResponseEntity.ok(noticeService.getNoticesByDateRange(from, to));
    }

    // 6️⃣ Search notices by TITLE / KEYWORD
    // GET /api/cns/notices/search?keyword=exam
    @GetMapping("/search")
    public ResponseEntity<List<Notice>> searchNotices(@RequestParam String keyword) {
        return ResponseEntity.ok(noticeService.searchNoticesByTitle(keyword));
    }

    // 7️⃣ Upload notice FILE
    // POST /api/cns/notices/file/upload
    @PostMapping(
            value = "/file/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<FileUploadResponse> uploadFile(@ModelAttribute NoticeUploadRequest request) {
        try {
            return ResponseEntity.ok(noticeService.uploadFile(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new FileUploadResponse(false, null, null, e.getMessage()));
        }
    }

    // 8️⃣ View notice FILE
    // GET /api/cns/notices/file/view/{noticeId}
    @GetMapping("/file/view/{noticeId}")
    public ResponseEntity<?> viewFile(@PathVariable Long noticeId) {
        try {
            String fileUrl = noticeService.getFileViewUrl(noticeId);
            return ResponseEntity.ok(Map.of("fileUrl", fileUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 9️⃣ Delete notice FILE
    // DELETE /api/cns/notices/file/{noticeId}
    @DeleteMapping("/file/{noticeId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long noticeId) {
        try {
            noticeService.deleteFileByNoticeId(noticeId);
            return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 🔟 Update notice ACTIVE / INACTIVE status (Admin)
    // PUT /api/cns/notices/{id}/status?isActive=true
    @PutMapping("/{id}/status")
    public ResponseEntity<Notice> updateStatus(
            @PathVariable Long id,
            @RequestParam boolean isActive
    ) {
        return ResponseEntity.ok(noticeService.updateAdminStatus(id, isActive));
    }
}
