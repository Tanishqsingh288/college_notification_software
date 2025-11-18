package com.college.notification.dto;

import com.college.notification.model.Notice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeResponseDTO {
    private Long id;
    private String title;
    private String content;
    private Long teacherId;
    private String teacherName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NoticeResponseDTO fromEntity(Notice n) {
        return NoticeResponseDTO.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getBody())
                .teacherId(n.getTeacher().getId())
                .teacherName(n.getTeacher().getFullName())
                .createdAt(n.getCreatedAt())
                .updatedAt(n.getUpdatedAt())
                .build();
    }
}
