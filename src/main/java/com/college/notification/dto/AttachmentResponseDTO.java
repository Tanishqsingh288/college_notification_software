package com.college.notification.dto;

import com.college.notification.model.Attachment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AttachmentResponseDTO {
    private Long id;
    private String fileName;
    private String fileUrl;
    private Long noticeId;
    private LocalDateTime uploadedAt;

    public static AttachmentResponseDTO fromEntity(Attachment a) {
        return AttachmentResponseDTO.builder()
                .id(a.getId())
                .fileName(a.getFileName())
                .fileUrl(a.getFileUrl())
                .noticeId(a.getNotice().getId())
                .uploadedAt(a.getUploadedAt())
                .build();
    }
}
