package com.college.notification.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class NoticeUploadRequest {

    private String title;          // optional - if null, filename will be used
    private String description;    // optional
    private String keyword;        // optional

    @NotNull(message = "validTill date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "validTill must be today or in the future")
    private LocalDate validTill;

    @NotNull(message = "uploadedByName is required")
    private String uploadedByName;

    @NotNull(message = "uploaderId is required")
    private Long uploaderId;

    @NotNull(message = "deptId is required")
    private Long deptId;

    @NotNull(message = "deptName is required")
    private String deptName;

    @NotNull(message = "file is required")
    private MultipartFile file;  // ✅ Add file here

    // Convert LocalDate to Instant (end of day)
    public Instant getValidTillAsInstant() {
        if (validTill == null) return null;
        return validTill.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();
    }

    // Getters & Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public LocalDate getValidTill() { return validTill; }
    public void setValidTill(LocalDate validTill) { this.validTill = validTill; }

    public String getUploadedByName() { return uploadedByName; }
    public void setUploadedByName(String uploadedByName) { this.uploadedByName = uploadedByName; }

    public Long getUploaderId() { return uploaderId; }
    public void setUploaderId(Long uploaderId) { this.uploaderId = uploaderId; }

    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
}
