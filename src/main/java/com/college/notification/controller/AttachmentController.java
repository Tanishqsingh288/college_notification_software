package com.college.notification.controller;

import com.college.notification.dto.AttachmentResponseDTO;
import com.college.notification.model.Attachment;
import com.college.notification.service.AttachmentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    // ------------------------
    // UPLOAD ATTACHMENT
    // ------------------------
    @PostMapping("/notices/{id}/attachments")
    public AttachmentResponseDTO upload(@PathVariable Long id,
                                        @RequestParam("file") MultipartFile file) throws IOException {
        Attachment attachment = attachmentService.uploadAttachment(id, file);
        return AttachmentResponseDTO.fromEntity(attachment);
    }

    // ------------------------
    // DOWNLOAD ATTACHMENT
    // ------------------------
    @GetMapping("/attachments/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Attachment attachment = attachmentService.getAttachmentById(id);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + attachment.getFileName() + "\"");

        try (FileInputStream in = new FileInputStream(attachment.getFileUrl());
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }
}
