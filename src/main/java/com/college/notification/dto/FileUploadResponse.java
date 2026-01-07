package com.college.notification.dto;

import java.time.Instant;

public class FileUploadResponse {

    private boolean success;
    private String fileUrl;
    private Long noticeId;
    private String message;

    // Additional metadata
    private String title;
    private String description;
    private Instant validTill;
    private String uploadedByName;
    private Long uploaderId;
    private Long deptId;
    private String deptName;
    private String keyword;

    public FileUploadResponse(boolean success, String fileUrl, Long noticeId, String message) {
        this.success = success;
        this.fileUrl = fileUrl;
        this.noticeId = noticeId;
        this.message = message;
    }

    // getters & setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public Long getNoticeId() { return noticeId; }
    public void setNoticeId(Long noticeId) { this.noticeId = noticeId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getValidTill() { return validTill; }
    public void setValidTill(Instant validTill) { this.validTill = validTill; }

    public String getUploadedByName() { return uploadedByName; }
    public void setUploadedByName(String uploadedByName) { this.uploadedByName = uploadedByName; }

    public Long getUploaderId() { return uploaderId; }
    public void setUploaderId(Long uploaderId) { this.uploaderId = uploaderId; }

    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}
