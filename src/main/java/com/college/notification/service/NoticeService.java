package com.college.notification.service;

import com.college.notification.dto.FileUploadResponse;
import com.college.notification.dto.NoticeUploadRequest;
import com.college.notification.entity.Notice;
import com.college.notification.repository.NoticeRepository;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private DbxClientV2 dbxClient;

    private final String DROPBOX_FOLDER = "/notices";

    // 1) List all notices
    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc();
    }

    // 2) List active notices
    public List<Notice> getActiveNotices() {
        return noticeRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    // 3) List non-active notices
    public List<Notice> getNonActiveNotices() {
        return noticeRepository.findByIsActiveFalseOrderByCreatedAtDesc();
    }

    // 4) List notices by department
    public List<Notice> getNoticesByDept(Long deptId) {
        return noticeRepository.findByDeptIdOrderByCreatedAtDesc(deptId);
    }

    // 5) List notices by date range
    public List<Notice> getNoticesByDateRange(Instant from, Instant to) {
        return noticeRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to);
    }

    // 6) Search notices by title
    public List<Notice> searchNoticesByTitle(String title) {
        return noticeRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(title);
    }
    public FileUploadResponse uploadFile(NoticeUploadRequest request) throws Exception {

        MultipartFile file = request.getFile();
        if (file == null || file.isEmpty()) {
            throw new Exception("File is empty or missing");
        }

        // Sanitize filename
        String originalName = file.getOriginalFilename();
        String safeFileName = originalName == null ? "file" : originalName.replaceAll("\\s+", "_");

        // 1️⃣ Save metadata first
        Notice notice = new Notice();
        notice.setTitle(request.getTitle() != null ? request.getTitle() : safeFileName);
        notice.setDescription(request.getDescription());
        notice.setKeyword(request.getKeyword());
        notice.setValidTill(request.getValidTillAsInstant());
        notice.setUploadedByName(request.getUploadedByName());
        notice.setUploaderId(request.getUploaderId());
        notice.setDeptId(request.getDeptId());
        notice.setDeptName(request.getDeptName());
        notice.setCreatedAt(Instant.now());

        notice = noticeRepository.save(notice); // Save to get ID

        // 2️⃣ Prepare Dropbox path
        String dropboxPath = DROPBOX_FOLDER + "/" + notice.getId() + "_" + safeFileName;

        try (InputStream inputStream = file.getInputStream()) {

            // 3️⃣ Upload file to Dropbox
            FileMetadata metadata = dbxClient.files()
                    .uploadBuilder(dropboxPath)
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(inputStream);

            // 4️⃣ Create or fetch shared link
            String sharedUrl;
            try {
                sharedUrl = dbxClient.sharing()
                        .createSharedLinkWithSettings(metadata.getPathLower())
                        .getUrl();
            } catch (DbxException e) {
                // Link already exists → fetch existing one
                sharedUrl = dbxClient.sharing()
                        .listSharedLinksBuilder()
                        .withPath(metadata.getPathLower())
                        .withDirectOnly(true)
                        .start()
                        .getLinks()
                        .get(0)
                        .getUrl();
            }

            // 5️⃣ Force direct download
            String fileUrl = sharedUrl.replace("?dl=0", "?dl=1");

            // 6️⃣ Update notice with file URL
            notice.setFileUrl(fileUrl);
            noticeRepository.save(notice);

            // 7️⃣ Prepare response with full metadata
            FileUploadResponse response = new FileUploadResponse(true, fileUrl, notice.getId(), "Upload successful");
            response.setTitle(notice.getTitle());
            response.setDescription(notice.getDescription());
            response.setValidTill(notice.getValidTill());
            response.setUploadedByName(notice.getUploadedByName());
            response.setUploaderId(notice.getUploaderId());
            response.setDeptId(notice.getDeptId());
            response.setDeptName(notice.getDeptName());
            response.setKeyword(notice.getKeyword());

            return response;

        } catch (DbxException e) {
            throw new Exception("Dropbox upload failed: " + e.getMessage());
        }
    }
    public String getFileViewUrl(Long noticeId) throws Exception {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception("Notice not found"));

        if (notice.getFileUrl() == null || notice.getFileUrl().isEmpty()) {
            throw new Exception("No file attached to this notice");
        }

        return notice.getFileUrl();
    }

    public void deleteFileByNoticeId(Long noticeId) throws Exception {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception("Notice not found"));

        String fileUrl = notice.getFileUrl();
        if (fileUrl != null && !fileUrl.isEmpty()) {
            try {
                // Use Dropbox API to get actual path from shared link
                String dropboxPath = dbxClient.sharing().getSharedLinkMetadata(fileUrl).getPathLower();

                dbxClient.files().deleteV2(dropboxPath);

                // Clear URL in database
                notice.setFileUrl(null);
                noticeRepository.save(notice);

            } catch (DbxException e) {
                throw new Exception("Failed to delete file from Dropbox: " + e.getMessage());
            }
        } else {
            throw new Exception("No file attached to this notice");
        }
    }


    public List<Notice> listAllNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Notice> listActiveNotices() {
        return noticeRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    public List<Notice> listInactiveNotices() {
        return noticeRepository.findByIsActiveFalseOrderByCreatedAtDesc();
    }

    public List<Notice> listNoticesByKeyword(String keyword) {
        return noticeRepository.findByKeywordContainingIgnoreCase(keyword);
    }

    public Notice updateAdminStatus(Long noticeId, boolean isActive) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
        notice.setIsActive(isActive);
        return noticeRepository.save(notice);
    }











}
