package com.college.notification.service;
import com.college.notification.entity.Query;
import com.lowagie.text.*;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
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

            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, baos);
            document.open();

            // ---------------- Header ----------------
            Font headerFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph header = new Paragraph("Disha College\nRam Nagar, Kota, Raipur, CG", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            document.add(Chunk.NEWLINE);

            // ---------------- Title ----------------
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Query Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(Chunk.NEWLINE);

            // ---------------- Table ----------------
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{2f, 5f}); // column widths

            Font fieldFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font valueFont = new Font(Font.HELVETICA, 12);

            addRow(table, "Query ID", String.valueOf(query.getId()), fieldFont, valueFont);
            addRow(table, "Subject", query.getTitle(), fieldFont, valueFont);
            addRow(table, "Query", query.getDescription(), fieldFont, valueFont);
            addRow(table, "Sender", query.getSentByEmail(), fieldFont, valueFont);
            addRow(table, "Current Status", query.isResolved() ? "Resolved" : "Unresolved", fieldFont, valueFont);
            addRow(table, "Resolved By", query.getResolvedBy() != null ? query.getResolvedBy() : "-", fieldFont, valueFont);
            addRow(table, "Sent At", formatDate(query.getCreatedAt()), fieldFont, valueFont);
            addRow(table, "Resolved At", query.getResolvedAt() != null ? formatDate(query.getResolvedAt()) : "-", fieldFont, valueFont);

            // Time Taken
            String timeTaken = calculateTimeTaken(query.getCreatedAt(), query.getResolvedAt());
            addRow(table, "Time Taken", timeTaken, fieldFont, valueFont);

            // Remarks
            addRow(table, "Remarks", "", fieldFont, valueFont);

            document.add(table);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addRow(PdfPTable table, String field, String value, Font fieldFont, Font valueFont) {
        PdfPCell cell1 = new PdfPCell(new Phrase(field, fieldFont));
        cell1.setPadding(5);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase(value != null ? value : "", valueFont));
        cell2.setPadding(5);
        table.addCell(cell2);
    }

    private String calculateTimeTaken(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return "-";

        Duration duration = Duration.between(start, end);
        long totalMinutes = duration.toMinutes();

        if (totalMinutes < 60) {
            return totalMinutes + " mins";
        } else {
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;
            return hours + " hrs " + minutes + " mins";
        }
    }

    private String formatDate(LocalDateTime dateTime) {
        return dtf.format(dateTime);
    }
}
