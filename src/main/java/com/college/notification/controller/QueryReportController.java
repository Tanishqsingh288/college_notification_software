package com.college.notification.controller;

import com.college.notification.service.ConsolidatedQueryReportPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/query-reports")
@CrossOrigin(origins = "*")
public class QueryReportController {

    @Autowired
    private ConsolidatedQueryReportPdfService queryReportService;

    /**
     * Generate consolidated query report with filters
     *
     * @param filterType - "all", "resolved", or "unresolved"
     * @param startDate - Optional start date (yyyy-MM-dd)
     * @param endDate - Optional end date (yyyy-MM-dd)
     */
    @GetMapping("/generate")
    public ResponseEntity<Resource> generateQueryReport(
            @RequestParam(defaultValue = "all") String filterType,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws IOException {

        // Convert LocalDate to LocalDateTime (start of day to end of day)
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (startDate != null) {
            start = startDate.atStartOfDay(); // 00:00:00
        }

        if (endDate != null) {
            end = endDate.atTime(LocalTime.MAX); // 23:59:59.999999999
        }

        // Generate PDF
        byte[] pdfBytes = queryReportService.generateQueryReport(filterType, start, end);

        // Create filename
        String filename = createFilename(filterType, startDate, endDate);

        // Return PDF
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(new ByteArrayResource(pdfBytes));
    }

    /**
     * Create descriptive filename
     */
    private String createFilename(String filterType, LocalDate startDate, LocalDate endDate) {
        StringBuilder filename = new StringBuilder("query_report_");
        filename.append(filterType.toLowerCase());

        if (startDate != null && endDate != null) {
            filename.append("_")
                    .append(startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .append("_to_")
                    .append(endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        } else if (startDate != null) {
            filename.append("_from_")
                    .append(startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        } else if (endDate != null) {
            filename.append("_until_")
                    .append(endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }

        filename.append(".pdf");
        return filename.toString();
    }

    /**
     * Simple test endpoint
     */
    @GetMapping("/test")
    public String testEndpoint() {
        return "Query Report API is working! Time: " + LocalDateTime.now();
    }
}