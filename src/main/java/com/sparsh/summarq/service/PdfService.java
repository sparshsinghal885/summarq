package com.sparsh.summarq.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generateSummaryPdf(String markdownText) {
        try {
            // Convert Markdown to HTML
            Parser parser = Parser.builder().build();
            HtmlRenderer renderer = HtmlRenderer.builder().build();

            Node document = parser.parse(markdownText);
            String htmlText = renderer.render(document); // Corrected Markdown to HTML conversion

            // Create a PDF in memory
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document pdfDocument = new Document();
            PdfWriter.getInstance(pdfDocument, outputStream);
            pdfDocument.open();

            // Process Markdown and Add to PDF
            addMarkdownToPdf(pdfDocument, markdownText);

            pdfDocument.close();
            return outputStream.toByteArray(); // Return PDF as byte array
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private void addMarkdownToPdf(Document document, String markdownText) throws DocumentException {
        String[] lines = markdownText.split("\n");

        for (String line : lines) {
            if (line.startsWith("## ")) { // H2 Heading
                Font headingFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
                document.add(new Paragraph(line.replace("## ", ""), headingFont));
            } else if (line.startsWith("### ")) { // H3 Subheading
                Font subHeadingFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
                document.add(new Paragraph(line.replace("### ", ""), subHeadingFont));
            } else if (line.startsWith("* ")) { // Bullet Points
                // Append manually formatted bullet points
                Font bulletFont = new Font(Font.FontFamily.HELVETICA, 12);
                document.add(new Paragraph("• " + line.replace("* ", ""), bulletFont));
            } else if (!line.trim().isEmpty()) { // Regular text
                Font textFont = new Font(Font.FontFamily.HELVETICA, 12);
                document.add(new Paragraph(line, textFont));
            }

            document.add(new Paragraph("\n")); // Add spacing
        }
    }
}
