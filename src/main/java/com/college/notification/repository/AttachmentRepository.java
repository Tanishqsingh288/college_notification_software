package com.college.notification.repository;

import com.college.notification.model.Attachment;
import com.college.notification.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findAllByNotice(Notice notice);
}
