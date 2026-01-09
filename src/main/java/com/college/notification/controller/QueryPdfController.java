package com.college.notification.controller;

import com.college.notification.entity.Query;
import com.college.notification.repository.QueryRepository;
import com.college.notification.service.QueryPdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pdf")
public class QueryPdfController {

    private final QueryRepository queryRepository;
    private final QueryPdfService pdfService;

    public QueryPdfController(QueryRepository queryRepository, QueryPdfService pdfService) {
        this.queryRepository = queryRepository;
        this.pdfService = pdfService;
    }

    @GetMapping("/query/{id}")
    public ResponseEntity<byte[]> downloadQueryPdf(@PathVariable Long id) {
        Query query = queryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Query not found with id " + id));

        byte[] pdfBytes = pdfService.generateQueryPdf(query);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=query_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
