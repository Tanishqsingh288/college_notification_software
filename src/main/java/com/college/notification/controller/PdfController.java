package com.college.notification.controller;

import com.college.notification.service.NoticePdfService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "*")
public class PdfController {

    private final NoticePdfService noticePdfService;

    public PdfController(NoticePdfService noticePdfService) {
        this.noticePdfService = noticePdfService;
    }

    @GetMapping("/department/{deptId}/report")
    public ResponseEntity<ByteArrayResource> generateDepartmentReport(@PathVariable Long deptId) {
        try {
            byte[] pdfBytes = noticePdfService.generateDepartmentNoticeReport(deptId);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            String fileName = "department_notice_report_" + deptId + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}