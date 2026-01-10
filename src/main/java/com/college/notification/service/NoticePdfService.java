package com.college.notification.service;

import com.college.notification.entity.*;
import com.college.notification.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NoticePdfService {

    private final NoticeRepository noticeRepository;
    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;

    public NoticePdfService(
            NoticeRepository noticeRepository,
            TeacherRepository teacherRepository,
            DepartmentRepository departmentRepository) {
        this.noticeRepository = noticeRepository;
        this.teacherRepository = teacherRepository;
        this.departmentRepository = departmentRepository;
    }

    public byte[] generateDepartmentNoticeReport(Long deptId) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Fetch data
            Department dept = departmentRepository.findById(deptId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            List<Teacher> teachers = teacherRepository.findByDeptId(deptId);
            List<Notice> notices = noticeRepository.findByDeptIdOrderByCreatedAtAsc(deptId);

            // Fonts
            Font headerFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(0, 51, 102));
            Font collegeFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(0, 102, 0));
            Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(51, 51, 51));
            Font labelFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.BLACK);
            Font valueFont = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
            Font tableHeaderFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            // ============ PAGE 1: DEPARTMENT SUMMARY ============

            // College Header with Border
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingAfter(15f);

            PdfPCell headerCell = new PdfPCell();
            headerCell.setBorder(Rectangle.BOX);
            headerCell.setBorderWidth(2);
            headerCell.setBorderColor(new Color(0, 51, 102));
            headerCell.setPadding(10);
            headerCell.setBackgroundColor(new Color(240, 248, 255));

            Paragraph collegeName = new Paragraph("DISHA COLLEGE", headerFont);
            collegeName.setAlignment(Element.ALIGN_CENTER);

            Paragraph collegeAddress = new Paragraph("Ram Nagar, Kota, Raipur, Chhattisgarh", collegeFont);
            collegeAddress.setAlignment(Element.ALIGN_CENTER);

            headerCell.addElement(collegeName);
            headerCell.addElement(collegeAddress);
            headerTable.addCell(headerCell);
            document.add(headerTable);

            // Main Title
            Paragraph mainTitle = new Paragraph("DEPARTMENT WISE NOTICE REPORT", titleFont);
            mainTitle.setAlignment(Element.ALIGN_CENTER);
            mainTitle.setSpacingAfter(20f);
            document.add(mainTitle);

            // Department Information Section
            Paragraph deptSection = new Paragraph("DEPARTMENT INFORMATION", sectionFont);
            deptSection.setSpacingAfter(10f);
            document.add(deptSection);

            // Department Info Table
            PdfPTable deptInfoTable = createInfoTable(2);
            addCompactInfoRow(deptInfoTable, "Department ID", String.valueOf(dept.getId()), labelFont, valueFont);
            addCompactInfoRow(deptInfoTable, "Department Name", dept.getName(), labelFont, valueFont);
            addCompactInfoRow(deptInfoTable, "Total Notices", String.valueOf(notices.size()), labelFont, valueFont);
            addCompactInfoRow(deptInfoTable, "Report Generated On", currentDate, labelFont, valueFont);
            document.add(deptInfoTable);
            document.add(Chunk.NEWLINE);

            // Teachers Section
            Paragraph teachersSection = new Paragraph("TEACHERS IN THIS DEPARTMENT", sectionFont);
            teachersSection.setSpacingAfter(10f);
            document.add(teachersSection);

            if (teachers.isEmpty()) {
                Paragraph noTeachers = new Paragraph("No teachers found in this department.", valueFont);
                noTeachers.setIndentationLeft(20);
                document.add(noTeachers);
            } else {
                PdfPTable teachersTable = new PdfPTable(2);
                teachersTable.setWidthPercentage(100);
                teachersTable.setSpacingBefore(8f);
                teachersTable.setSpacingAfter(15f);

                // Table Headers
                PdfPCell uidHeader = new PdfPCell(new Paragraph("TEACHER UID", tableHeaderFont));
                uidHeader.setBackgroundColor(new Color(0, 102, 153));
                uidHeader.setPadding(8);
                uidHeader.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell nameHeader = new PdfPCell(new Paragraph("TEACHER NAME", tableHeaderFont));
                nameHeader.setBackgroundColor(new Color(0, 102, 153));
                nameHeader.setPadding(8);
                nameHeader.setHorizontalAlignment(Element.ALIGN_CENTER);

                teachersTable.addCell(uidHeader);
                teachersTable.addCell(nameHeader);

                // Alternate row colors
                Color rowColor1 = new Color(255, 255, 255);
                Color rowColor2 = new Color(248, 248, 248);

                for (int i = 0; i < teachers.size(); i++) {
                    Teacher teacher = teachers.get(i);
                    Color rowColor = (i % 2 == 0) ? rowColor1 : rowColor2;

                    PdfPCell uidCell = new PdfPCell(new Paragraph(teacher.getUid(), valueFont));
                    uidCell.setBackgroundColor(rowColor);
                    uidCell.setPadding(6);

                    PdfPCell nameCell = new PdfPCell(new Paragraph(teacher.getName(), valueFont));
                    nameCell.setBackgroundColor(rowColor);
                    nameCell.setPadding(6);

                    teachersTable.addCell(uidCell);
                    teachersTable.addCell(nameCell);
                }
                document.add(teachersTable);
            }

            // ============ NOTICE PAGES (One per notice) ============
            if (!notices.isEmpty()) {
                int noticeCount = 1;
                int totalNotices = notices.size();

                for (Notice notice : notices) {
                    // New page for each notice
                    document.newPage();

                    // Header for each notice page
                    PdfPTable noticeHeaderTable = new PdfPTable(1);
                    noticeHeaderTable.setWidthPercentage(100);
                    noticeHeaderTable.setSpacingAfter(15f);

                    PdfPCell noticeHeaderCell = new PdfPCell();
                    noticeHeaderCell.setBorder(Rectangle.BOX);
                    noticeHeaderCell.setBorderWidth(2);
                    noticeHeaderCell.setBorderColor(new Color(0, 51, 102));
                    noticeHeaderCell.setPadding(10);
                    noticeHeaderCell.setBackgroundColor(new Color(240, 248, 255));

                    Paragraph collegeNameNotice = new Paragraph("DISHA COLLEGE",
                            new Font(Font.HELVETICA, 16, Font.BOLD, new Color(0, 51, 102)));
                    collegeNameNotice.setAlignment(Element.ALIGN_CENTER);

                    Paragraph collegeAddressNotice = new Paragraph("Ram Nagar, Kota, Raipur, Chhattisgarh",
                            new Font(Font.HELVETICA, 11, Font.NORMAL, Color.DARK_GRAY));
                    collegeAddressNotice.setAlignment(Element.ALIGN_CENTER);

                    noticeHeaderCell.addElement(collegeNameNotice);
                    noticeHeaderCell.addElement(collegeAddressNotice);
                    noticeHeaderTable.addCell(noticeHeaderCell);
                    document.add(noticeHeaderTable);

                    // Notice Title
                    Paragraph noticeTitle = new Paragraph("NOTICE DETAILS",
                            new Font(Font.HELVETICA, 18, Font.BOLD, new Color(0, 102, 0)));
                    noticeTitle.setAlignment(Element.ALIGN_CENTER);

                    Paragraph noticeNumber = new Paragraph("NOTICE #" + noticeCount + " of " + totalNotices,
                            new Font(Font.HELVETICA, 12, Font.BOLD, new Color(102, 0, 0)));
                    noticeNumber.setAlignment(Element.ALIGN_CENTER);
                    noticeNumber.setSpacingAfter(15f);

                    document.add(noticeTitle);
                    document.add(noticeNumber);

                    // Notice Information Section
                    Paragraph infoSection = new Paragraph("NOTICE INFORMATION",
                            new Font(Font.HELVETICA, 14, Font.BOLD, new Color(51, 51, 51)));
                    infoSection.setSpacingAfter(10f);
                    document.add(infoSection);

                    // Notice Info Table
                    PdfPTable noticeInfoTable = new PdfPTable(2);
                    noticeInfoTable.setWidthPercentage(100);
                    noticeInfoTable.setSpacingBefore(5f);
                    noticeInfoTable.setSpacingAfter(10f);

                    String createdDate = notice.getCreatedAt() != null
                            ? dateFormatter.format(notice.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime())
                            : "-";

                    String validUntil = notice.getValidTill() != null
                            ? dateFormatter.format(notice.getValidTill().atZone(ZoneId.systemDefault()).toLocalDateTime())
                            : "Not specified";

                    addCompactInfoRow(noticeInfoTable, "Notice ID", String.valueOf(notice.getId()), labelFont, valueFont);
                    addCompactInfoRow(noticeInfoTable, "Date Created", createdDate, labelFont, valueFont);
                    addCompactInfoRow(noticeInfoTable, "Title", notice.getTitle(), labelFont, valueFont);
                    addCompactInfoRow(noticeInfoTable, "Department", notice.getDeptName(), labelFont, valueFont);
                    addCompactInfoRow(noticeInfoTable, "Session", notice.getSession(), labelFont, valueFont);
                    addCompactInfoRow(noticeInfoTable, "Uploaded By", notice.getUploadedByName(), labelFont, valueFont);
                    addCompactInfoRow(noticeInfoTable, "Valid Until", validUntil, labelFont, valueFont);

                    document.add(noticeInfoTable);

                    // Description Section
                    if (notice.getDescription() != null && !notice.getDescription().trim().isEmpty()) {
                        Paragraph descSection = new Paragraph("DESCRIPTION",
                                new Font(Font.HELVETICA, 14, Font.BOLD, new Color(51, 51, 51)));
                        descSection.setSpacingAfter(5f);
                        document.add(descSection);

                        PdfPTable descTable = new PdfPTable(1);
                        descTable.setWidthPercentage(100);
                        descTable.setSpacingBefore(3f);
                        descTable.setSpacingAfter(10f);

                        PdfPCell descCell = new PdfPCell(new Paragraph(notice.getDescription(), valueFont));
                        descCell.setBorder(Rectangle.BOX);
                        descCell.setBorderWidth(1);
                        descCell.setBorderColor(Color.LIGHT_GRAY);
                        descCell.setPadding(10);
                        descCell.setBackgroundColor(new Color(255, 255, 240));

                        descTable.addCell(descCell);
                        document.add(descTable);
                    }

                    // Remarks and Signature Section
                    document.add(Chunk.NEWLINE);

                    Paragraph remarksSection = new Paragraph("REMARKS",
                            new Font(Font.HELVETICA, 14, Font.BOLD, new Color(51, 51, 51)));
                    remarksSection.setSpacingAfter(5f);
                    document.add(remarksSection);

                    // Signature area - on same page
                    Paragraph signatureLine = new Paragraph("___________________________________________",
                            new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY));
                    signatureLine.setSpacingBefore(30f);
                    signatureLine.setAlignment(Element.ALIGN_CENTER);

                    Paragraph signatureLabel = new Paragraph("Signature",
                            new Font(Font.HELVETICA, 11, Font.ITALIC, Color.DARK_GRAY));
                    signatureLabel.setAlignment(Element.ALIGN_CENTER);

                    document.add(signatureLine);
                    document.add(signatureLabel);

                    noticeCount++;
                }
            } else {
                // If no notices found
                document.newPage();

                // Re-add header
                document.add(headerTable);

                Paragraph noNoticesTitle = new Paragraph("NOTICE DETAILS",
                        new Font(Font.HELVETICA, 18, Font.BOLD, new Color(0, 102, 0)));
                noNoticesTitle.setAlignment(Element.ALIGN_CENTER);
                noNoticesTitle.setSpacingAfter(20f);
                document.add(noNoticesTitle);

                Paragraph noNoticesMsg = new Paragraph("No notices found for this department.",
                        new Font(Font.HELVETICA, 14, Font.BOLD, Color.RED));
                noNoticesMsg.setAlignment(Element.ALIGN_CENTER);
                noNoticesMsg.setSpacingAfter(20f);
                document.add(noNoticesMsg);
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate notice PDF", e);
        }
    }

    // ==================== HELPER METHODS ====================

    private PdfPTable createInfoTable(int columns) {
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(100);
        table.setSpacingBefore(8f);
        table.setSpacingAfter(15f);
        return table;
    }

    private void addCompactInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        if (value == null || value.trim().isEmpty()) {
            value = "-";
        }

        PdfPCell labelCell = new PdfPCell(new Paragraph(label + ":", labelFont));
        labelCell.setBorder(Rectangle.BOX);
        labelCell.setBorderWidth(1);
        labelCell.setBorderColor(Color.LIGHT_GRAY);
        labelCell.setPadding(6);
        labelCell.setBackgroundColor(new Color(245, 245, 245));

        PdfPCell valueCell = new PdfPCell(new Paragraph(value, valueFont));
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setBorderWidth(1);
        valueCell.setBorderColor(Color.LIGHT_GRAY);
        valueCell.setPadding(6);
        valueCell.setBackgroundColor(Color.WHITE);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}