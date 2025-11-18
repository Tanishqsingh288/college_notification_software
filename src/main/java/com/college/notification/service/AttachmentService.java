package com.college.notification.service;

import com.college.notification.model.Attachment;
import com.college.notification.model.Notice;
import com.college.notification.repository.AttachmentRepository;
import com.college.notification.service.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final NoticeService noticeService;

    private static final String UPLOAD_DIR = "uploads/";

    // ------------------------
    // UPLOAD ATTACHMENT
    // ------------------------
    public Attachment uploadAttachment(Long noticeId, MultipartFile file) throws IOException {

        Notice notice = noticeService.getNoticeById(noticeId);

        // Create upload dir if not exists
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        // Save file to local storage
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(dir, filename);
        file.transferTo(dest);

        // Save metadata in DB
        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .fileUrl(dest.getAbsolutePath())
                .notice(notice)
                .build();

        return attachmentRepository.save(attachment);
    }

    // ------------------------
    // LIST ATTACHMENTS FOR NOTICE
    // ------------------------
    public List<Attachment> getAttachmentsByNotice(Long noticeId) {
        Notice notice = noticeService.getNoticeById(noticeId);
        return attachmentRepository.findAllByNotice(notice);
    }

    // ------------------------
    // GET ATTACHMENT BY ID
    // ------------------------
    public Attachment getAttachmentById(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new AuthException("Attachment not found"));
    }
}
