package com.college.notification.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_notice_view")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentNoticeView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @PrePersist
    protected void onView() {
        viewedAt = LocalDateTime.now();
    }
}
