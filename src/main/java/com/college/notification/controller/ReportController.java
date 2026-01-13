package com.college.notification.controller;

import com.college.notification.service.ConsolidatedNoticeReportPdfService;
import com.college.notification.entity.Notice;
import com.college.notification.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ConsolidatedNoticeReportPdfService pdfService;

    @Autowired
    private NoticeRepository noticeRepository;

    /**
     * SINGLE API: Generate PDF summary report for specific department
     * GET /api/reports/department/{deptId}
     */
    @GetMapping("/department/{deptId}")
    public ResponseEntity<Resource> generateDepartmentReport(@PathVariable Long deptId) throws IOException {
        // Generate PDF report
        byte[] pdfBytes = pdfService.generateDepartmentReport(deptId);

        // Get department name for filename
        String deptName = getDepartmentName(deptId);
        String safeDeptName = deptName.toLowerCase()
                .replace(" ", "_")
                .replace("/", "_")
                .replace("\\", "_");

        // Create filename with department name and ID
        String filename = safeDeptName + "_summary_" + deptId + ".pdf";

        // Return PDF
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(new ByteArrayResource(pdfBytes));
    }

    /**
     * Helper method to get department name
     */
    private String getDepartmentName(Long deptId) {
        // Get one notice from this department to extract department name
        List<Notice> notices = noticeRepository.findByDeptIdOrderByCreatedAtDesc(deptId);
        if (!notices.isEmpty()) {
            return notices.get(0).getDeptName();
        }
        return "Department_" + deptId;
    }
}