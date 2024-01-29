package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.Report;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
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

public class ReportPDF {
    private static final Color INVOICE_LIGHT_BLUE = new DeviceRgb(232, 243, 251);
    private static final Color INVOICE_BLUE = new DeviceRgb(136, 193, 232);
    private static final Color INVOICE_WHITE = new DeviceRgb(255, 255, 255);
    private static final Border BLUE_BORDER = new SolidBorder(INVOICE_BLUE, 0.5f);
    private static PdfFont FONT_RUBIK_SEMIBOLD = null;
    private static PdfFont FONT_POPPINS = null;

    public Document generateReport(Window window, Report report) throws IOException {
        String path = getSaveLocation(window);

        PDFDocument pdfDocument = new PDFDocument();
        Document document = pdfDocument.getDocument(path);

        // Images
        Image reportHeading = loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/marketing-head-report-1.png");

        // Fonts
        FONT_RUBIK_SEMIBOLD = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Rubik-SemiBold.ttf");
        FONT_POPPINS = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Poppins-Regular.ttf");

        // Table 01
        Table table01 = getTable01(reportHeading);
        Table table02 = getTable02(report.getPayee());
        Table table03 = getTable03(report.getPayee());

        document.add(table01); // Header
        document.add(table02); // Report ID
        document.add(table03); // Report Month, Artist, Gross Rev, and Partner share amount

        document.close();

        return document;
    }

    private Table getTable03(String payee) {
        // Table
        float[] columnWidth = {200f, 5f, 200f, 5f, 200f, 200f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);

        // Row 01
        table.addCell(new Cell().add(new Paragraph("Month").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(16f).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(INVOICE_BLUE).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph(""))
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("Artist").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(16f).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(INVOICE_BLUE).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph(""))
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("Gross Revenue Produced").setFont(FONT_POPPINS))
                .setPaddingLeft(5f).setVerticalAlignment(VerticalAlignment.MIDDLE).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph("{AMOUNT}").setFont(FONT_POPPINS))
                .setPaddingLeft(5f).setVerticalAlignment(VerticalAlignment.MIDDLE).setFontSize(10f).setBorder(BLUE_BORDER));

        // Row 02
        table.addCell(new Cell().add(new Paragraph("{MONTH}").setFont(FONT_POPPINS))
                .setFontSize(10f).setTextAlignment(TextAlignment.CENTER).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph(""))
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("{ARTIST}").setFont(FONT_POPPINS))
                .setFontSize(10f).setTextAlignment(TextAlignment.CENTER).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph(""))
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("Partner Revenue Share").setFont(FONT_POPPINS))
                .setPaddingLeft(5f).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph("{PRS AMOUNT}").setFont(FONT_POPPINS))
                .setPaddingLeft(5f).setFontSize(10f).setBorder(BLUE_BORDER));

        return table;
    }

    private static Table getTable02(String payee) {
        // Table 02
        float[] columnWidth = {500f, 500f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);

        // Table 02 Row 01
        table.addCell(new Cell().add(new Paragraph("CLIENT REPORT")
                        .setFontSize(28f)
                        .setFontColor(INVOICE_BLUE)
                        .setFont(FONT_RUBIK_SEMIBOLD))
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(DatabaseMySQL.getReportNumber(payee))
                        .setFontSize(16f)
                        .setFont(FONT_RUBIK_SEMIBOLD))
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER));
        return table;
    }

    private static Table getTable01(Image reportHeading) {
        float[] columnWidth = {1000f};
        Table table = new Table(columnWidth);

        //<editor-fold desc="Header">
        // Table 01 Row 01
        table.addCell(new Cell().add(reportHeading).setBorder(Border.NO_BORDER).setMargin(0f));
        // Table 01 Row 02
        table.addCell(new Cell()
                .add(new Paragraph(""))
                .setBackgroundColor(INVOICE_LIGHT_BLUE)
                .setBorder(Border.NO_BORDER)
        );
        //</editor-fold>
        return table;
    }

    private static String getSaveLocation(Window window) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        chooser.setTitle("Save As");
        File pathTo = chooser.showSaveDialog(window);
        return pathTo.getAbsolutePath();
    }
}
