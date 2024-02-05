package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.Model.ArtistReport;
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
import java.util.ArrayList;
import java.util.Iterator;

import static com.example.song_finder_fx.Controller.Invoice.loadAutoScaledImage;
import static com.example.song_finder_fx.Controller.Invoice.loadFont;

public class ReportPDF {
    private static final Color INVOICE_LIGHT_BLUE = new DeviceRgb(232, 243, 251);
    private static final Color INVOICE_BLUE = new DeviceRgb(136, 193, 232);
    private static final Color INVOICE_WHITE = new DeviceRgb(255, 255, 255);
    private static final Color INVOICE_BLACK = new DeviceRgb(25, 23, 22);
    private static final Color INVOICE_RED = new DeviceRgb(178, 110, 99);
    private static final Color INVOICE_GRAY = new DeviceRgb(205, 205, 205);
    private static final Border BLUE_BORDER = new SolidBorder(INVOICE_BLUE, 0.5f);
    private static final Border BLACK_BORDER = new SolidBorder(INVOICE_BLACK, 0.5f);
    private static PdfFont FONT_RUBIK_SEMIBOLD = null;
    private static PdfFont FONT_POPPINS = null;

    public void generateReport(Window window, ArtistReport report) throws IOException {
        String path = getSaveLocation(window);

        PDFDocument pdfDocument = new PDFDocument();
        Document document = pdfDocument.getDocument(path);

        // Images
        Image reportHeading = loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/marketing-head-report-1.png");

        // Fonts
        FONT_RUBIK_SEMIBOLD = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Rubik-SemiBold.ttf");
        FONT_POPPINS = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Poppins-Regular.ttf");

        // Table 01
        Table tableHeader = getHeaderTable(reportHeading);
        Table table02 = getTable02(report);
        Table table03 = getTable03(report);
        Table table04 = getTable04(report);
        Table tableCoWriterPaymentSummary = getCoWriterTable(report);
        Table tableFooter = getFooterTable();
        tableFooter.setFixedPosition(20f, document.getBottomMargin(), document.getWidth());

        document.add(tableHeader); // Header
        document.add(table02);
        document.add(table03);
        document.add(table04);
        document.add(tableCoWriterPaymentSummary); // Co-Writer Payment Summary
        document.add(tableCoWriterPaymentSummary); // Co-Writer Payment Summary
        document.add(tableFooter);

        document.close();
    }

    private Table getFooterTable() {
        // Table
        float[] columnWidth = {950f, 50f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);

        table.addCell(new Cell().add(new Paragraph("For statement inquiries or to change payment info, please contact our accounting team:")
                        .setFont(FONT_POPPINS)
                        .setFontSize(10f))
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(10f)
                .setBackgroundColor(INVOICE_GRAY)
                .setVerticalAlignment(VerticalAlignment.BOTTOM));
        table.addCell(new Cell(2, 1).add(new Paragraph("Page 02")
                        .setFont(FONT_POPPINS)
                        .setFontSize(10f))
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(INVOICE_GRAY)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(new Paragraph("admin@ceymusic.com.au | Ranura Perera: +94787090980")
                        .setFont(FONT_RUBIK_SEMIBOLD)
                        .setFontSize(10f))
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(10f)
                .setBackgroundColor(INVOICE_GRAY)
                .setVerticalAlignment(VerticalAlignment.TOP));

        return table;
    }

    private Table getCoWriterTable(ArtistReport report) {
        // Table
        float[] columnWidth = {500f, 500f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);

        // ArrayList
        ArrayList<String> coWriters = report.getCoWriters();
        ArrayList<String> coWriterShares = report.getCoWriterShare();
        int writerCount = coWriters.size();

        // Table 02 Row 01
        table.addCell(new Cell(1, 2).add(new Paragraph("Co-Writer Payment Summary").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(16f).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(INVOICE_BLUE).setBorder(BLUE_BORDER));

        for (int i = 0; i <= (writerCount - 1); i++) {
            table.addCell(new Cell().add(new Paragraph(coWriters.get(i)).setFont(FONT_POPPINS))
                    .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
            table.addCell(new Cell().add(new Paragraph(coWriterShares.get(i)).setFont(FONT_POPPINS))
                    .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        }

        /*// Table 02 Row 02
        table.addCell(new Cell().add(new Paragraph("Rukshan Mark").setFont(FONT_POPPINS))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph("Rs 2,972.32").setFont(FONT_POPPINS))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        // Table 02 Row 02
        table.addCell(new Cell().add(new Paragraph("Rukshan Mark").setFont(FONT_POPPINS))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph("Rs 2,972.32").setFont(FONT_POPPINS))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        // Table 02 Row 02
        table.addCell(new Cell().add(new Paragraph("Rukshan Mark").setFont(FONT_POPPINS))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph("Rs 2,972.32").setFont(FONT_POPPINS))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        // Table 02 Row 02
        table.addCell(new Cell().add(new Paragraph("Rukshan Mark").setFont(FONT_POPPINS))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph("Rs 2,972.32").setFont(FONT_POPPINS))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        // Table 02 Row 02
        table.addCell(new Cell().add(new Paragraph("Rukshan Mark").setFont(FONT_POPPINS))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph("Rs 2,972.32").setFont(FONT_POPPINS))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        // Table 02 Row 02
        table.addCell(new Cell().add(new Paragraph("Rukshan Mark").setFont(FONT_POPPINS))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph("Rs 2,972.32").setFont(FONT_POPPINS))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));*/

        return table;
    }

    private Table getTable04(ArtistReport report) {
        // Table
        float[] columnWidth = {300f, 5f, 200f, 5f, 300f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);

        // Row 01
        table.addCell(new Cell().add(new Paragraph("Partner Share of Gross").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(16f).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(INVOICE_BLACK).setBorder(BLACK_BORDER));
        table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("Amount Payable").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(16f).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(INVOICE_BLACK).setBorder(BLACK_BORDER));
        table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("14% TAX (If Applicable)").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(16f).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(INVOICE_RED).setBorder(BLACK_BORDER));

        // Row 02
        table.addCell(new Cell().add(new Paragraph(report.getPartnerShareInLKR()).setFont(FONT_POPPINS))
                .setFontSize(16f).setTextAlignment(TextAlignment.CENTER).setBorder(BLACK_BORDER));
        table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(report.getAmountPayable()).setFont(FONT_POPPINS))
                .setFontSize(16f).setTextAlignment(TextAlignment.CENTER).setBorder(BLACK_BORDER));
        table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(report.getTaxAmount()).setFont(FONT_POPPINS))
                .setFontSize(16f).setTextAlignment(TextAlignment.CENTER).setBorder(BLACK_BORDER));

        return table;
    }

    private Table getTable03(ArtistReport report) {
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
        table.addCell(new Cell().add(new Paragraph(report.getGrossRevenueInLKR()).setFont(FONT_POPPINS))
                .setPaddingLeft(5f).setVerticalAlignment(VerticalAlignment.MIDDLE).setFontSize(10f).setBorder(BLUE_BORDER));

        // Row 02
        table.addCell(new Cell().add(new Paragraph(report.getMonth()).setFont(FONT_POPPINS))
                .setFontSize(10f).setTextAlignment(TextAlignment.CENTER).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph(""))
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(report.getPayee()).setFont(FONT_POPPINS))
                .setFontSize(10f).setTextAlignment(TextAlignment.CENTER).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph(""))
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("Partner Revenue Share").setFont(FONT_POPPINS))
                .setPaddingLeft(5f).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph("90%").setFont(FONT_POPPINS))
                .setPaddingLeft(5f).setFontSize(10f).setBorder(BLUE_BORDER));

        return table;
    }

    private static Table getTable02(ArtistReport report) {
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
        table.addCell(new Cell().add(new Paragraph(report.getPayee())
                        .setFontSize(16f)
                        .setFont(FONT_RUBIK_SEMIBOLD))
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER));
        return table;
    }

    private static Table getHeaderTable(Image reportHeading) {
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
