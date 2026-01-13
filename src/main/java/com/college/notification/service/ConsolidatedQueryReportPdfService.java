package com.college.notification.service;

import com.college.notification.entity.Query;
import com.college.notification.repository.QueryRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ConsolidatedQueryReportPdfService {

    @Autowired
    private QueryRepository queryRepository;

    // Formatters
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final DateTimeFormatter HEADER_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter GENERATED_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    // Fonts
    private static final Font COLLEGE_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
    private static final Font ADDRESS_FONT =
            FontFactory.getFont(FontFactory.HELVETICA, 11, Color.DARK_GRAY);
    private static final Font TITLE_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(0, 102, 204));
    private static final Font PERIOD_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
    private static final Font TABLE_HEADER_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
    private static final Font TABLE_CONTENT_FONT =
            FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
    private static final Font RESOLVED_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(0, 128, 0));
    private static final Font UNRESOLVED_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(204, 0, 0));
    private static final Font FOOTER_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY);

    // Colors
    private static final Color TABLE_HEADER_BG = new Color(0, 102, 204);
    private static final Color ODD_ROW_BG = new Color(248, 248, 248);

    /**
     * Generate PDF report with filters
     */
    public byte[] generateQueryReport(String filterType, LocalDateTime startDate, LocalDateTime endDate)
            throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4); // A4 format
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        writer.setPageEvent(new PdfPageEvent());
        document.open();

        // Get queries based on filter
        List<Query> queries = getQueriesByFilter(filterType, startDate, endDate);

        // Add header with period information
        addReportHeader(document, filterType, startDate, endDate);

        // Add queries table
        addQueriesTable(document, queries);

        // Add footer with statistics
        addReportFooter(document, queries);

        document.close();
        return outputStream.toByteArray();
    }

    /**
     * Get queries based on filter type and date range
     */
    private List<Query> getQueriesByFilter(String filterType, LocalDateTime startDate, LocalDateTime endDate) {
        List<Query> queries;

        switch (filterType.toLowerCase()) {
            case "resolved":
                queries = queryRepository.findByResolvedTrue();
                break;
            case "unresolved":
                queries = queryRepository.findByResolvedFalse();
                break;
            default: // "all"
                queries = queryRepository.findAll();
                break;
        }

        // Apply date filter if dates provided
        if (startDate != null && endDate != null) {
            queries = queries.stream()
                    .filter(q -> !q.getCreatedAt().isBefore(startDate) &&
                            !q.getCreatedAt().isAfter(endDate))
                    .toList();
        }

        return queries;
    }

    /**
     * Add report header with period info
     */
    private void addReportHeader(Document document, String filterType,
                                 LocalDateTime startDate, LocalDateTime endDate)
            throws DocumentException {

        // College Name
        Paragraph college = new Paragraph("DISHA COLLEGE", COLLEGE_FONT);
        college.setAlignment(Element.ALIGN_CENTER);
        college.setSpacingAfter(5f);
        document.add(college);

        // Address
        Paragraph address = new Paragraph("RAMANAGR, KOTA, RAIPUR (C.G.)", ADDRESS_FONT);
        address.setAlignment(Element.ALIGN_CENTER);
        address.setSpacingAfter(10f);
        document.add(address);

        // Report Title
        Paragraph title = new Paragraph("CONSOLIDATED QUERY REPORTS", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5f);
        document.add(title);

        // Filter and Period Info
        StringBuilder periodInfo = new StringBuilder();
        periodInfo.append("Filter: ").append(getFilterDisplayName(filterType));

        if (startDate != null && endDate != null) {
            periodInfo.append(" | Period: ")
                    .append(startDate.format(HEADER_DATE_FORMATTER))
                    .append(" to ")
                    .append(endDate.format(HEADER_DATE_FORMATTER));
        }

        Paragraph period = new Paragraph(periodInfo.toString(), PERIOD_FONT);
        period.setAlignment(Element.ALIGN_CENTER);
        period.setSpacingAfter(15f);
        document.add(period);
    }

    /**
     * Get display name for filter
     */
    private String getFilterDisplayName(String filterType) {
        switch (filterType.toLowerCase()) {
            case "resolved": return "Resolved Queries Only";
            case "unresolved": return "Unresolved Queries Only";
            default: return "All Queries";
        }
    }

    /**
     * Add queries table (A4 optimized)
     */
    private void addQueriesTable(Document document, List<Query> queries)
            throws DocumentException {

        if (queries.isEmpty()) {
            Paragraph noData = new Paragraph("No queries found for selected criteria.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, Color.RED));
            noData.setAlignment(Element.ALIGN_CENTER);
            noData.setSpacingAfter(20f);
            document.add(noData);
            return;
        }

        // Create table with 4 columns for A4
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(15f);

        // A4 optimized column widths
        float[] columnWidths = {12f, 22f, 46f, 20f};
        table.setWidths(columnWidths);

        // Add table headers
        addTableHeader(table, "Query ID");
        addTableHeader(table, "Sent At");
        addTableHeader(table, "Subject");
        addTableHeader(table, "Status");

        // Add query rows
        int rowCount = 0;
        for (Query query : queries) {
            addQueryRow(table, query, rowCount);
            rowCount++;
        }

        document.add(table);
    }

    /**
     * Add table header
     */
    private void addTableHeader(PdfPTable table, String headerText) {
        PdfPCell cell = new PdfPCell(new Phrase(headerText, TABLE_HEADER_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(TABLE_HEADER_BG);
        cell.setPadding(6f);
        cell.setBorderWidth(0.5f);
        cell.setBorderColor(Color.LIGHT_GRAY);
        table.addCell(cell);
    }

    /**
     * Add query row
     */
    private void addQueryRow(PdfPTable table, Query query, int rowIndex) {
        // Query ID
        PdfPCell idCell = new PdfPCell(new Phrase(query.getId().toString(), TABLE_CONTENT_FONT));
        styleTableCell(idCell, rowIndex % 2 == 0 ? Color.WHITE : ODD_ROW_BG, Element.ALIGN_CENTER);
        table.addCell(idCell);

        // Sent At
        String sentAt = query.getCreatedAt().format(DATE_TIME_FORMATTER);
        PdfPCell dateCell = new PdfPCell(new Phrase(sentAt, TABLE_CONTENT_FONT));
        styleTableCell(dateCell, rowIndex % 2 == 0 ? Color.WHITE : ODD_ROW_BG, Element.ALIGN_CENTER);
        table.addCell(dateCell);

        // Subject (A4 optimized truncation)
        String subject = truncateText(query.getTitle(), 65);
        PdfPCell subjectCell = new PdfPCell(new Phrase(subject, TABLE_CONTENT_FONT));
        styleTableCell(subjectCell, rowIndex % 2 == 0 ? Color.WHITE : ODD_ROW_BG, Element.ALIGN_LEFT);
        table.addCell(subjectCell);

        // Status with color coding
        String status = query.isResolved() ? "Resolved" : "Unresolved";
        Font statusFont = query.isResolved() ? RESOLVED_FONT : UNRESOLVED_FONT;

        PdfPCell statusCell = new PdfPCell(new Phrase(status, statusFont));
        styleTableCell(statusCell,
                rowIndex % 2 == 0 ? Color.WHITE : ODD_ROW_BG,
                Element.ALIGN_CENTER);
        table.addCell(statusCell);
    }

    /**
     * Style table cell
     */
    private void styleTableCell(PdfPCell cell, Color bgColor, int alignment) {
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(bgColor);
        cell.setPadding(5f);
        cell.setBorderWidth(0.5f);
        cell.setBorderColor(Color.LIGHT_GRAY);
    }

    /**
     * Add report footer with statistics
     */
    private void addReportFooter(Document document, List<Query> queries)
            throws DocumentException {

        long total = queries.size();
        long resolved = queries.stream().filter(Query::isResolved).count();
        long unresolved = total - resolved;

        Paragraph footer = new Paragraph();
        footer.add(new Chunk("Total Queries: " + total + " | ", FOOTER_FONT));
        footer.add(new Chunk("Resolved: " + resolved + " | ", FOOTER_FONT));
        footer.add(new Chunk("Unresolved: " + unresolved + " | ", FOOTER_FONT));
        footer.add(new Chunk("Generated: " +
                LocalDateTime.now().format(GENERATED_DATE_FORMATTER), FOOTER_FONT));

        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10f);
        document.add(footer);
    }

    /**
     * Truncate text if too long
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) return "N/A";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
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