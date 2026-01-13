package com.college.notification.service;

import com.college.notification.entity.Notice;
import com.college.notification.repository.NoticeRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConsolidatedNoticeReportPdfService {

    @Autowired
    private NoticeRepository noticeRepository;

    // Formatters
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
    private static final DateTimeFormatter GENERATED_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    // Fonts
    private static final Font COLLEGE_NAME_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
    private static final Font COLLEGE_ADDRESS_FONT =
            FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY);
    private static final Font DEPARTMENT_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(0, 102, 204));
    private static final Font SESSION_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
    private static final Font TABLE_HEADER_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
    private static final Font TABLE_CONTENT_FONT =
            FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
    private static final Font FOOTER_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY);

    // Colors
    private static final Color TABLE_HEADER_BG = new Color(0, 102, 204); // Blue
    private static final Color ODD_ROW_BG = new Color(248, 248, 248); // Light gray
    private static final Color EVEN_ROW_BG = Color.WHITE;

    /**
     * Generate PDF for all departments
     */
    public byte[] generateAllDepartmentsReport() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        writer.setPageEvent(new PdfPageEvent());
        document.open();

        Map<String, List<Notice>> departmentNotices = getAllNoticesGroupedByDepartment();

        int deptCount = 0;
        for (Map.Entry<String, List<Notice>> entry : departmentNotices.entrySet()) {
            if (deptCount > 0) {
                document.newPage();
            }

            String deptName = entry.getKey();
            List<Notice> notices = entry.getValue();

            addDepartmentHeader(document, deptName);
            addNoticeTable(document, notices);
            addFooter(document, deptName, notices.size());

            deptCount++;
        }

        document.close();
        return outputStream.toByteArray();
    }

    /**
     * Generate PDF for specific department
     */
    public byte[] generateDepartmentReport(Long deptId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        writer.setPageEvent(new PdfPageEvent());
        document.open();

        List<Notice> notices = getNoticesByDepartment(deptId);
        String deptName = notices.isEmpty() ? "Unknown Department" : notices.get(0).getDeptName();

        addDepartmentHeader(document, deptName);
        addNoticeTable(document, notices);
        addFooter(document, deptName, notices.size());

        document.close();
        return outputStream.toByteArray();
    }

    /**
     * Get all notices grouped by department
     */
    private Map<String, List<Notice>> getAllNoticesGroupedByDepartment() {
        List<Notice> activeNotices = noticeRepository.findByIsActiveTrueOrderByCreatedAtDesc();

        return activeNotices.stream()
                .collect(Collectors.groupingBy(
                        Notice::getDeptName,
                        Collectors.toList()
                ));
    }

    /**
     * Get notices for specific department
     */
    private List<Notice> getNoticesByDepartment(Long deptId) {
        return noticeRepository.findByDeptIdOrderByCreatedAtDesc(deptId);
    }

    /**
     * Add department header
     */
    private void addDepartmentHeader(Document document, String deptName) throws DocumentException {
        // College Name
        Paragraph collegeName = new Paragraph("DISHA COLLEGE", COLLEGE_NAME_FONT);
        collegeName.setAlignment(Element.ALIGN_CENTER);
        collegeName.setSpacingAfter(5f);
        document.add(collegeName);

        // College Address
        Paragraph address = new Paragraph("RAMANAGR, KOTA, RAIPUR (C.G.)", COLLEGE_ADDRESS_FONT);
        address.setAlignment(Element.ALIGN_CENTER);
        address.setSpacingAfter(10f);
        document.add(address);

        // Department Name
        Paragraph department = new Paragraph(deptName + " DEPARTMENT", DEPARTMENT_FONT);
        department.setAlignment(Element.ALIGN_CENTER);
        department.setSpacingAfter(5f);
        document.add(department);

        // Session
        Paragraph session = new Paragraph("NOTICE SUMMARY", SESSION_FONT);
        session.setAlignment(Element.ALIGN_CENTER);
        session.setSpacingAfter(15f);
        document.add(session);
    }

    /**
     * Add notice table (A4 optimized)
     */
    private void addNoticeTable(Document document, List<Notice> notices) throws DocumentException {
        if (notices.isEmpty()) {
            Paragraph noData = new Paragraph("No notices found for this department.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, Color.RED));
            noData.setAlignment(Element.ALIGN_CENTER);
            noData.setSpacingAfter(20f);
            document.add(noData);
            return;
        }

        // Create table with 5 columns for A4
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(20f);

        // A4 optimized column widths
        float[] columnWidths = {10f, 20f, 40f, 15f, 15f};
        table.setWidths(columnWidths);

        // Add table headers
        addTableHeader(table, "Notice ID");
        addTableHeader(table, "Uploaded At");
        addTableHeader(table, "Title");
        addTableHeader(table, "Session");
        addTableHeader(table, "Uploaded By");

        // Add notice rows
        int rowCount = 0;
        for (Notice notice : notices) {
            addNoticeRow(table, notice, rowCount % 2 == 0 ? EVEN_ROW_BG : ODD_ROW_BG);
            rowCount++;
        }

        document.add(table);
    }

    /**
     * Add table header cell
     */
    private void addTableHeader(PdfPTable table, String headerText) {
        PdfPCell cell = new PdfPCell(new Phrase(headerText, TABLE_HEADER_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(TABLE_HEADER_BG);
        cell.setPadding(8f);
        cell.setBorderWidth(1f);
        table.addCell(cell);
    }

    /**
     * Add notice row
     */
    private void addNoticeRow(PdfPTable table, Notice notice, Color bgColor) {
        // Notice ID
        PdfPCell idCell = new PdfPCell(new Phrase(notice.getId().toString(), TABLE_CONTENT_FONT));
        styleTableCell(idCell, bgColor, Element.ALIGN_CENTER);
        table.addCell(idCell);

        // Uploaded At
        String uploadedAt = formatDateTime(notice.getCreatedAt());
        PdfPCell dateCell = new PdfPCell(new Phrase(uploadedAt, TABLE_CONTENT_FONT));
        styleTableCell(dateCell, bgColor, Element.ALIGN_CENTER);
        table.addCell(dateCell);

        // Title (truncated for A4)
        String title = truncateTitle(notice.getTitle(), 70);
        PdfPCell titleCell = new PdfPCell(new Phrase(title, TABLE_CONTENT_FONT));
        styleTableCell(titleCell, bgColor, Element.ALIGN_LEFT);
        table.addCell(titleCell);

        // Session
        String session = notice.getSession() != null ? notice.getSession() : "N/A";
        PdfPCell sessionCell = new PdfPCell(new Phrase(session, TABLE_CONTENT_FONT));
        styleTableCell(sessionCell, bgColor, Element.ALIGN_CENTER);
        table.addCell(sessionCell);

        // Uploaded By
        String uploadedBy = truncateTitle(notice.getUploadedByName(), 15);
        PdfPCell uploaderCell = new PdfPCell(new Phrase(uploadedBy, TABLE_CONTENT_FONT));
        styleTableCell(uploaderCell, bgColor, Element.ALIGN_CENTER);
        table.addCell(uploaderCell);
    }

    /**
     * Style table cell
     */
    private void styleTableCell(PdfPCell cell, Color bgColor, int alignment) {
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6f);
        cell.setBorderWidth(0.5f);
        cell.setBorderColor(Color.LIGHT_GRAY);
    }

    /**
     * Add footer
     */
    private void addFooter(Document document, String deptName, int noticeCount) throws DocumentException {
        Paragraph footer = new Paragraph();
        footer.add(new Chunk("Department: " + deptName + " | ", FOOTER_FONT));
        footer.add(new Chunk("Total Notices: " + noticeCount + " | ", FOOTER_FONT));
        footer.add(new Chunk("Generated: " + LocalDateTime.now().format(GENERATED_DATE_FORMATTER), FOOTER_FONT));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10f);
        document.add(footer);
    }

    /**
     * Format date-time
     */
    private String formatDateTime(Instant instant) {
        if (instant == null) return "N/A";
        return DATE_TIME_FORMATTER.format(
                LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        );
    }

    /**
     * Truncate title
     */
    private String truncateTitle(String title, int maxLength) {
        if (title == null) return "N/A";
        if (title.length() <= maxLength) return title;
        return title.substring(0, maxLength - 3) + "...";
    }

    /**
     * Inner class for page events
     */
    private static class PdfPageEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase footer = new Phrase("Page " + writer.getPageNumber(),
                    FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY));
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }
}