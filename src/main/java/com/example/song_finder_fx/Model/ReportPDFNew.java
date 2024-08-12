package com.example.song_finder_fx.Model;

import com.example.song_finder_fx.Constants.Colors;
import com.example.song_finder_fx.Controller.PDFDocument;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Objects;

public class ReportPDFNew implements Colors {
    private static PdfFont FONT_RUBIK_SEMIBOLD = null;
    private static PdfFont FONT_POPPINS = null;
    private static PdfFont FONT_POPPINS_MEDIUM = null;
    private static final Border DARK_BLUE_BORDER = new SolidBorder(INVOICE_DARK_BLUE, 0.5f);

    public void generateReport(String path, ArtistReport report) throws IOException {
        PDFDocument pdfDocument = new PDFDocument();
        Document document = pdfDocument.getDocument(path);

        // Images
        Image reportHeading = getArtistHeading(report.getArtist().getName());

        // Fonts
        FONT_RUBIK_SEMIBOLD = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Rubik-SemiBold.ttf");
        FONT_POPPINS = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Poppins-Regular.ttf");
        FONT_POPPINS_MEDIUM = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Poppins-Medium.ttf");

        // Tables
        Table tableHeader = getHeaderTable(reportHeading);
        Table reportSummary = getReportSummaryTable(report);
        reportSummary.setFixedPosition(35, 570, 540);
        Table songBreakdown = getSongBreakdownTable(report);
        // songBreakdown.setFixedPosition(35, 10, 540);

        SolidLine line = new SolidLine(90f);
        line.setColor(INVOICE_DARK_BLUE);

        // Add text on top of the letter head
        Paragraph reportMonthAndYear = new Paragraph(report.getMonth().toUpperCase() + " " + report.getYear())
                .setFont(FONT_RUBIK_SEMIBOLD)
                .setFontSize(20)
                .setFontColor(INVOICE_BLUE);
        reportMonthAndYear.setFixedPosition(28, 760, 600);

        document.add(tableHeader); // Letter Head
        document.add(reportMonthAndYear); // Report Month and Year
        document.add(reportSummary);
        document.add(getSongBreakdownTable(report));

        document.close();
    }

    private Table getSongBreakdownTable(ArtistReport report) throws MalformedURLException {
        /*float[] columnWidth = {50f, 350f, 50f, 50f, 50f, 50f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);

        PdfFont titleFont = FONT_POPPINS;
        float titleFontSize = 10f;
        Border border = DARK_BLUE_BORDER;

        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(border));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(border));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Splits"))
                        .setFontSize(titleFontSize)
                        .setFontColor(INVOICE_DARK_BLUE)
                .setFont(titleFont).setBorder(border));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Tracks"))
                        .setFontSize(titleFontSize)
                        .setFontColor(INVOICE_DARK_BLUE)
                .setFont(titleFont).setBorder(border));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Artist Share (AUD)"))
                        .setFontSize(titleFontSize)
                        .setFontColor(INVOICE_DARK_BLUE)
                .setFont(titleFont).setBorder(border));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Artist Share (LKR)"))
                        .setFontSize(titleFontSize)
                        .setFontColor(INVOICE_DARK_BLUE)
                .setFont(titleFont).setBorder(border));

        return table;*/

        // Table
        float[] columnWidth = {50f, 350f, 50f, 50f, 50f, 50f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        float fontSizeSubTitle = 15f;
        PdfFont subtitleFont = FONT_POPPINS_MEDIUM;

        Image image = loadImage("src/main/resources/com/example/song_finder_fx/images/reports/icon_grow.png", false);
        image.setWidth(10);

        // Row 01
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Splits")).setBorder(Border.NO_BORDER));
        // table.addCell(new Cell().setHeight(20f).add(image).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Tracks").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_DARK_BLUE).setFontSize(10f).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Artist Share (AUD)")).setBorder(Border.NO_BORDER));
        // table.addCell(new Cell().setHeight(20f).add(image).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Artist Share (LKR)").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_DARK_BLUE).setFontSize(10f).setBorder(Border.NO_BORDER));

        return table;
    }

    private Table getReportSummaryTable(ArtistReport report) throws MalformedURLException {
        // Table
        float[] columnWidth = {5f, 200f, 5f, 5f, 200f, 5f, 5f, 200f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        float fontSizeSubTitle = 15f;
        PdfFont subtitleFont = FONT_POPPINS_MEDIUM;

        Image image = loadImage("src/main/resources/com/example/song_finder_fx/images/reports/icon_grow.png", false);
        image.setWidth(10);

        // Row 01
        table.addCell(new Cell().setHeight(20f).add(image).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Gross Revenue Produced").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_DARK_BLUE).setFontSize(10f).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(image).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Partner Share of Gross").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_DARK_BLUE).setFontSize(10f).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(image).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Amount Payable").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_DARK_BLUE).setFontSize(10f).setBorder(Border.NO_BORDER));

        // Row 02
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell()
                .add(new Paragraph("AUD " + String.format("%,9.2f", report.getGrossRevenueInAUD())).setFont(subtitleFont))
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(fontSizeSubTitle)
                .setFontColor(INVOICE_DARK_BLUE)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell()
                .add(new Paragraph("AUD " + String.format("%,9.2f", report.getPartnerShareInAUD())).setFont(subtitleFont))
                .setFontColor(INVOICE_DARK_BLUE)
                .setFontSize(fontSizeSubTitle)
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell()
                .add(new Paragraph("LKR " + String.format("%,9.2f", report.getPartnerShareInLKR())).setFont(subtitleFont))
                .setFontColor(INVOICE_DARK_BLUE)
                .setFontSize(fontSizeSubTitle)
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER));

        return table;
    }

    private Table getHeaderTable(Image reportHeading) {
        float[] columnWidth = {1000f};
        Table table = new Table(columnWidth);

        table.addCell(new Cell().add(reportHeading).setBorder(Border.NO_BORDER).setMargin(0f));

        return table;
    }

    static PdfFont loadFont(String location) throws IOException {
        return PdfFontFactory.createFont(FontProgramFactory.createFont(location), PdfEncodings.WINANSI, true);
    }

    static Image getArtistHeading(String artistName) throws MalformedURLException {
        Image invoiceHeading = new Image(ImageDataFactory.create(getArtistHeadingImage(artistName)));
        invoiceHeading.setAutoScale(true);
        return invoiceHeading;
    }

    static Image loadImage(String location, boolean autoscale) throws MalformedURLException {
        Image image = new Image(ImageDataFactory.create(location));
        image.setAutoScale(autoscale);
        return image;
    }

    private static String getArtistHeadingImage(String artistName) {
        if (Objects.equals(artistName, "Ridma Weerawardena")) {
            return "src/main/resources/com/example/song_finder_fx/images/reports/artists/ridmaw.png";
        } else {
            return "src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png";
        }
    }
}
