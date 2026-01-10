package com.college.notification.service;
import com.college.notification.entity.Query;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class QueryPdfService {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public byte[] generateQueryPdf(Query query) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 40, 40, 50, 40);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            // ==================== FONTS ====================
            Font headerFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(0, 51, 102));
            Font collegeFont = new Font(Font.HELVETICA, 14, Font.NORMAL, Color.DARK_GRAY);
            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(0, 102, 0));
            Font sectionFont = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(51, 51, 51));
            Font subSectionFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(70, 70, 70));
            Font labelFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLACK);
            Font valueFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);
            Font statusFont = new Font(Font.HELVETICA, 13, Font.BOLD);
            Font remarksFont = new Font(Font.HELVETICA, 11, Font.ITALIC, Color.DARK_GRAY);

            // ==================== HEADER ====================
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingAfter(20f);

            PdfPCell headerCell = new PdfPCell();
            headerCell.setBorder(Rectangle.BOX);
            headerCell.setBorderWidth(2);
            headerCell.setBorderColor(new Color(0, 51, 102));
            headerCell.setPadding(15);
            headerCell.setBackgroundColor(new Color(240, 248, 255));

            Paragraph collegeName = new Paragraph("DISHA COLLEGE", headerFont);
            collegeName.setAlignment(Element.ALIGN_CENTER);

            Paragraph collegeAddress = new Paragraph("Ram Nagar, Kota, Raipur, Chhattisgarh", collegeFont);
            collegeAddress.setAlignment(Element.ALIGN_CENTER);

            headerCell.addElement(collegeName);
            headerCell.addElement(collegeAddress);
            headerTable.addCell(headerCell);
            document.add(headerTable);

            // ==================== TITLE ====================
            Paragraph title = new Paragraph("QUERY RESOLUTION REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(25f);
            document.add(title);

            // ==================== QUERY INFO SECTION ====================
            PdfPTable infoSectionTable = new PdfPTable(1);
            infoSectionTable.setWidthPercentage(100);
            infoSectionTable.setSpacingAfter(15f);

            PdfPCell infoHeaderCell = createSectionHeaderCell("QUERY INFORMATION");
            infoSectionTable.addCell(infoHeaderCell);

            PdfPCell infoContentCell = new PdfPCell();
            infoContentCell.setBorder(Rectangle.NO_BORDER);
            infoContentCell.setPadding(10);
            infoContentCell.setPaddingTop(5);

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(5f);

            addStyledRow(infoTable, "Query ID",String.valueOf(query.getId()), labelFont, valueFont);

            // Status with color coding
            String statusText = query.isResolved() ? "RESOLVED" : "UNRESOLVED";
            Color statusColor = query.isResolved() ? new Color(0, 128, 0) : new Color(204, 0, 0);
            Font statusValueFont = new Font(Font.HELVETICA, 12, Font.BOLD, statusColor);
            addStyledRow(infoTable, "Current Status", statusText, labelFont, statusValueFont);

            // Priority (if you add this field to Query entity)
            // addStyledRow(infoTable, "Priority", "HIGH", labelFont, valueFont);

            infoContentCell.addElement(infoTable);
            infoSectionTable.addCell(infoContentCell);
            document.add(infoSectionTable);

            // ==================== QUERY DETAILS SECTION ====================
            PdfPTable detailsSectionTable = new PdfPTable(1);
            detailsSectionTable.setWidthPercentage(100);
            detailsSectionTable.setSpacingAfter(15f);

            PdfPCell detailsHeaderCell = createSectionHeaderCell("QUERY DETAILS");
            detailsSectionTable.addCell(detailsHeaderCell);

            PdfPCell detailsContentCell = new PdfPCell();
            detailsContentCell.setBorder(Rectangle.NO_BORDER);
            detailsContentCell.setPadding(15);

            // Subject
            Paragraph subjectLabel = new Paragraph("Subject:", labelFont);
            subjectLabel.setSpacingAfter(5f);
            detailsContentCell.addElement(subjectLabel);

            Paragraph subjectValue = new Paragraph(query.getTitle(),
                    new Font(Font.HELVETICA, 13, Font.BOLD, new Color(0, 0, 139)));
            subjectValue.setSpacingAfter(15f);
            detailsContentCell.addElement(subjectValue);

            // Query Description
            Paragraph queryLabel = new Paragraph("Query Description:", labelFont);
            queryLabel.setSpacingAfter(5f);
            detailsContentCell.addElement(queryLabel);

            PdfPTable descTable = new PdfPTable(1);
            descTable.setWidthPercentage(100);

            PdfPCell descCell = new PdfPCell(new Paragraph(query.getDescription(), valueFont));
            descCell.setBorder(Rectangle.BOX);
            descCell.setBorderWidth(1);
            descCell.setBorderColor(Color.LIGHT_GRAY);
            descCell.setPadding(12);
            descCell.setBackgroundColor(new Color(255, 255, 240));
            descTable.addCell(descCell);

            detailsContentCell.addElement(descTable);
            detailsSectionTable.addCell(detailsContentCell);
            document.add(detailsSectionTable);

            // ==================== TIMELINE SECTION ====================
            PdfPTable timelineSectionTable = new PdfPTable(1);
            timelineSectionTable.setWidthPercentage(100);
            timelineSectionTable.setSpacingAfter(15f);

            PdfPCell timelineHeaderCell = createSectionHeaderCell("TIMELINE");
            timelineSectionTable.addCell(timelineHeaderCell);

            PdfPCell timelineContentCell = new PdfPCell();
            timelineContentCell.setBorder(Rectangle.NO_BORDER);
            timelineContentCell.setPadding(15);

            PdfPTable timelineTable = new PdfPTable(2);
            timelineTable.setWidthPercentage(100);

            addStyledRow(timelineTable, "Sent At", formatDate(query.getCreatedAt()), labelFont, valueFont);

            if (query.getResolvedAt() != null) {
                addStyledRow(timelineTable, "Resolved At", formatDate(query.getResolvedAt()), labelFont, valueFont);
            }

            String timeTaken = calculateTimeTaken(query.getCreatedAt(), query.getResolvedAt());
            addStyledRow(timelineTable, "Resolution Time", timeTaken, labelFont, valueFont);

            timelineContentCell.addElement(timelineTable);
            timelineSectionTable.addCell(timelineContentCell);
            document.add(timelineSectionTable);

            // ==================== RESOLUTION DETAILS SECTION ====================
            PdfPTable resolutionSectionTable = new PdfPTable(1);
            resolutionSectionTable.setWidthPercentage(100);
            resolutionSectionTable.setSpacingAfter(15f);

            PdfPCell resolutionHeaderCell = createSectionHeaderCell("RESOLUTION DETAILS");
            resolutionSectionTable.addCell(resolutionHeaderCell);

            PdfPCell resolutionContentCell = new PdfPCell();
            resolutionContentCell.setBorder(Rectangle.NO_BORDER);
            resolutionContentCell.setPadding(15);

            PdfPTable resolutionTable = new PdfPTable(2);
            resolutionTable.setWidthPercentage(100);

            addStyledRow(resolutionTable, "Query Sender", query.getSentByEmail(), labelFont, valueFont);

            if (query.getResolvedBy() != null && !query.getResolvedBy().isEmpty()) {
                addStyledRow(resolutionTable, "Resolved By", query.getResolvedBy(), labelFont, valueFont);
            }

            resolutionContentCell.addElement(resolutionTable);
            resolutionSectionTable.addCell(resolutionContentCell);
            document.add(resolutionSectionTable);

            // ==================== REMARKS SECTION ====================
            PdfPTable remarksSectionTable = new PdfPTable(1);
            remarksSectionTable.setWidthPercentage(100);
            remarksSectionTable.setSpacingAfter(20f);

            PdfPCell remarksHeaderCell = createSectionHeaderCell("RESOLUTION REMARKS");
            remarksSectionTable.addCell(remarksHeaderCell);

            PdfPCell remarksContentCell = new PdfPCell();
            remarksContentCell.setBorder(Rectangle.BOX);
            remarksContentCell.setBorderWidth(1);
            remarksContentCell.setBorderColor(Color.LIGHT_GRAY);
            remarksContentCell.setPadding(20);
            remarksContentCell.setMinimumHeight(100);
            remarksContentCell.setBackgroundColor(new Color(250, 250, 250));

            // Add some placeholder text or actual remarks if you have that field
            Paragraph remarksPlaceholder = new Paragraph(
                    query.isResolved() ?
                            "" :
                            "",
                    new Font(Font.HELVETICA, 11, Font.ITALIC, Color.GRAY)
            );
            remarksContentCell.addElement(remarksPlaceholder);

            remarksSectionTable.addCell(remarksContentCell);
            document.add(remarksSectionTable);

            // ==================== SIGNATURE SECTION ====================
            PdfPTable signatureSectionTable = new PdfPTable(1);
            signatureSectionTable.setWidthPercentage(100);
            signatureSectionTable.setSpacingAfter(10f);

            PdfPCell signatureHeaderCell = createSectionHeaderCell("SIGNATURES");
            signatureSectionTable.addCell(signatureHeaderCell);

            PdfPCell signatureContentCell = new PdfPCell();
            signatureContentCell.setBorder(Rectangle.NO_BORDER);
            signatureContentCell.setPadding(20);

            // Resolving Authority Signature
            Paragraph resolvingAuth = new Paragraph("RESOLVING AUTHORITY",
                    new Font(Font.HELVETICA, 11, Font.BOLD, new Color(0, 51, 102)));
            resolvingAuth.setSpacingAfter(5f);
            signatureContentCell.addElement(resolvingAuth);

            PdfPTable sigTable1 = new PdfPTable(2);
            sigTable1.setWidthPercentage(100);
            sigTable1.setSpacingAfter(30f);

            PdfPCell sigLineCell1 = new PdfPCell(new Paragraph(
                    "___________________________________________",
                    new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY)));
            sigLineCell1.setBorder(Rectangle.NO_BORDER);
            sigLineCell1.setColspan(2);
            sigTable1.addCell(sigLineCell1);

            PdfPCell sigLabelCell1 = new PdfPCell(new Paragraph("Signature & Stamp", remarksFont));
            sigLabelCell1.setBorder(Rectangle.NO_BORDER);
            sigLabelCell1.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell dateCell1 = new PdfPCell(new Paragraph("Date: ___________", remarksFont));
            dateCell1.setBorder(Rectangle.NO_BORDER);
            dateCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);

            sigTable1.addCell(sigLabelCell1);
            sigTable1.addCell(dateCell1);
            signatureContentCell.addElement(sigTable1);

            // Sender Acknowledgment
            Paragraph senderAck = new Paragraph("QUERY SENDER ACKNOWLEDGMENT",
                    new Font(Font.HELVETICA, 11, Font.BOLD, new Color(0, 51, 102)));
            senderAck.setSpacingAfter(5f);
            signatureContentCell.addElement(senderAck);

            PdfPTable sigTable2 = new PdfPTable(2);
            sigTable2.setWidthPercentage(100);

            PdfPCell sigLineCell2 = new PdfPCell(new Paragraph(
                    "___________________________________________",
                    new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY)));
            sigLineCell2.setBorder(Rectangle.NO_BORDER);
            sigLineCell2.setColspan(2);
            sigTable2.addCell(sigLineCell2);

            PdfPCell sigLabelCell2 = new PdfPCell(new Paragraph("Signature", remarksFont));
            sigLabelCell2.setBorder(Rectangle.NO_BORDER);
            sigLabelCell2.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell dateCell2 = new PdfPCell(new Paragraph("Date: ___________", remarksFont));
            dateCell2.setBorder(Rectangle.NO_BORDER);
            dateCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);

            sigTable2.addCell(sigLabelCell2);
            sigTable2.addCell(dateCell2);
            signatureContentCell.addElement(sigTable2);

            signatureSectionTable.addCell(signatureContentCell);
            document.add(signatureSectionTable);

            // ==================== FOOTER ====================
            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph(
                    "This is an auto-generated query resolution report. For any discrepancies, contact the college administration.",
                    new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==================== HELPER METHODS ====================

    private PdfPCell createSectionHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text,
                new Font(Font.HELVETICA, 14, Font.BOLD, Color.WHITE)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(new Color(0, 102, 153));
        cell.setPadding(10);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private void addStyledRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Paragraph(label, labelFont));
        labelCell.setBorder(Rectangle.BOX);
        labelCell.setBorderWidth(1);
        labelCell.setBorderColor(Color.LIGHT_GRAY);
        labelCell.setPadding(8);
        labelCell.setBackgroundColor(new Color(245, 245, 245));

        PdfPCell valueCell = new PdfPCell(new Paragraph(value != null ? value : "-", valueFont));
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setBorderWidth(1);
        valueCell.setBorderColor(Color.LIGHT_GRAY);
        valueCell.setPadding(8);
        valueCell.setBackgroundColor(Color.WHITE);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private String calculateTimeTaken(LocalDateTime start, LocalDateTime end) {
        if (start == null) return "-";
        if (end == null) return "Ongoing";

        Duration duration = Duration.between(start, end);
        long totalMinutes = duration.toMinutes();

        if (totalMinutes < 60) {
            return totalMinutes + " minutes";
        } else if (totalMinutes < 1440) { // less than 24 hours
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;
            return hours + " hours " + (minutes > 0 ? minutes + " minutes" : "");
        } else {
            long days = totalMinutes / 1440;
            long remainingMinutes = totalMinutes % 1440;
            long hours = remainingMinutes / 60;
            return days + " days " + (hours > 0 ? hours + " hours" : "");
        }
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "-";
        return dtf.format(dateTime);
    }
}