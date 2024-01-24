package com.example.song_finder_fx;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;

import static com.example.song_finder_fx.Invoice.loadAutoScaledImage;
import static com.example.song_finder_fx.Invoice.loadFont;

public class Report {
    private static final Color INVOICE_LIGHT_BLUE = new DeviceRgb(232, 243, 251);
    private static final Color INVOICE_BLUE = new DeviceRgb(136, 193, 232);

    public Document generateReport(Window window, String payee) throws IOException {
        String path = getSaveLocation(window);

        PDFDocument pdfDocument = new PDFDocument();
        Document document = pdfDocument.getDocument(path);

        // Images
        Image reportHeading = loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/marketing-head-report-1.png");

        // Fonts
        PdfFont font_rubik = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Rubik-Regular.ttf");
        PdfFont font_rubik_semi_bold = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Rubik-SemiBold.ttf");
        PdfFont font_poppins = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Poppins-Regular.ttf");

        // Table 01
        float[] columnWidth = {1000f};
        Table table01 = new Table(columnWidth);

        //<editor-fold desc="Header">
        // Table 01 Row 01
        table01.addCell(new Cell().add(reportHeading).setBorder(Border.NO_BORDER).setMargin(0f));
        // Table 01 Row 02
        table01.addCell(new Cell()
                .add(new Paragraph(""))
                .setBackgroundColor(INVOICE_LIGHT_BLUE)
                .setBorder(Border.NO_BORDER)
        );
        //</editor-fold>

        // Table 02
        float[] columnWidthTable02 = {500f, 500f};
        Table table02 = new Table(columnWidthTable02);
        table02.setMarginLeft(20f);
        table02.setMarginRight(20f);

        // Table 02 Row 01
        table02.addCell(new Cell().add(new Paragraph("CLIENT REPORT")
                        .setFontSize(28f)
                        .setFontColor(INVOICE_BLUE)
                        .setFont(font_rubik_semi_bold))
                .setBorder(Border.NO_BORDER));
        table02.addCell(new Cell().add(new Paragraph(DatabaseMySQL.getReportNumber(payee))
                        .setFontSize(16f)
                        .setFont(font_rubik_semi_bold))
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER));

        document.add(table01); // Header
        document.add(table02); // Report ID

        document.close();

        return document;
    }

    private static String getSaveLocation(Window window) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        chooser.setTitle("Save As");
        File pathTo = chooser.showSaveDialog(window);
        return pathTo.getAbsolutePath();
    }
}
