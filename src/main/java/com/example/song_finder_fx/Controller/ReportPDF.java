package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.Model.ArtistReport;
import com.example.song_finder_fx.Model.CoWriterShare;
import com.example.song_finder_fx.Model.CoWriterSummary;
import com.example.song_finder_fx.Model.Songs;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.song_finder_fx.Controller.Invoice.loadAutoScaledImage;
import static com.example.song_finder_fx.Controller.Invoice.loadFont;

public class ReportPDF implements com.example.song_finder_fx.Constants.Colors {
    private static final Border BLUE_BORDER = new SolidBorder(INVOICE_BLUE, 0.5f);
    // private static final Border BLUE_BORDER_WO_SIDES = new SolidBorder()
    private static final Border BLACK_BORDER = new SolidBorder(INVOICE_BLACK, 0.5f);
    private static final Border DARK_BLUE_BORDER = new SolidBorder(INVOICE_DARK_BLUE, 0.5f);
    private static PdfFont FONT_RUBIK_SEMIBOLD = null;
    private static PdfFont FONT_POPPINS = null;

    /*public void generateReport(Window window, ArtistReport report) throws IOException {
        String path = getSaveLocation(window);

        PDFDocument pdfDocument = new PDFDocument();
        Document document = pdfDocument.getDocument(path);

        // Images
        Image reportHeading = loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png");

        // Fonts
        FONT_RUBIK_SEMIBOLD = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Rubik-SemiBold.ttf");
        FONT_POPPINS = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Poppins-Regular.ttf");

        // Table 01
        Table tableHeader = getHeaderTable(reportHeading);
        Table table02 = getTable02(report);
        Table table03 = getTable03(report);
        Table table04 = getTable04(report);
        Table tableCoWriterPaymentSummary = getCoWriterSummaryTable(report);
        Table tableTopPerformingSongsSummary = getTopPerformingSongsTable(report);
        Table tableFooter = getFooterTable();

        tableFooter.setFixedPosition(20f, document.getBottomMargin(), document.getWidth());

        document.add(tableHeader); // Header
        document.add(table02);
        document.add(table03);
        document.add(table04);
        document.add(tableCoWriterPaymentSummary); // Co-Writer Payment Summary
        document.add(tableTopPerformingSongsSummary); // Co-Writer Payment Summary
        document.add(tableFooter);

        document.close();
    }*/

    private Table getCoWriterSummaryTable(ArtistReport report) {
        float[] columnWidth = {500f, 200f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);

        // ArrayList<Songs> topPerformingSongs = report.getTopPerformingSongs();
        List<CoWriterSummary> coWriterSummaryList = report.getCoWriterPaymentSummary();
        // int songCount = topPerformingSongs.size();
        double eurToAudRate = report.getEurToAudRate();
        double AudToLkrRate = report.getAudToLkrRate();

        // Table 02 Row 01
        table.addCell(new Cell(1, 2).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 2).add(new Paragraph("CO-WRITER PAYMENT SUMMARY").setFont(FONT_RUBIK_SEMIBOLD))
                .setVerticalAlignment(VerticalAlignment.BOTTOM).setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10f).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 2).add(new Paragraph("")).setBorder(Border.NO_BORDER));

        table.addCell(new Cell().add(new Paragraph("ARTIST").setFont(FONT_RUBIK_SEMIBOLD).setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER).setBackgroundColor(INVOICE_BLUE));
        table.addCell(new Cell().add(new Paragraph("AMOUNT").setFont(FONT_RUBIK_SEMIBOLD).setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER).setBackgroundColor(INVOICE_BLUE));

        /*for (int i = 0; i <= (songCount -1); i++) {
            Songs song = topPerformingSongs.get(i);
            String trackTitle = song.getTrackTitle();
            double royalty = song.getRoyalty() * eurToAudRate;
            table.addCell(new Cell().add(new Paragraph(trackTitle).setFont(FONT_POPPINS))
                    .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
            table.addCell(new Cell().add(new Paragraph("LKR " + String.format("%,09.2f", royalty)).setFont(FONT_POPPINS))
                    .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        }*/

        for (CoWriterSummary summary : coWriterSummaryList) {
            double amount = summary.getRoyalty();
            double convertedRoyalty = amount * eurToAudRate * AudToLkrRate;

            String contributor = summary.getContributor();
            // String royaltyShare = "LKR " + String.format("%,09.2f", convertedRoyalty) + "/=";
            String royaltyShare = "LKR " + String.format("%,9.2f", convertedRoyalty) + "/=";

            table.addCell(new Cell().add(new Paragraph(contributor).setFont(FONT_POPPINS))
                    .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
            table.addCell(new Cell().add(new Paragraph(royaltyShare).setFont(FONT_POPPINS))
                    .setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        }

        return table;
    }

    private Table getTopPerformingSongsTable(ArtistReport report) {
        float[] columnWidth = {500f, 200f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);

        ArrayList<Songs> topPerformingSongs = report.getTopPerformingSongs();
        int songCount = topPerformingSongs.size();
        double eurToAudRate = report.getEurToAudRate();
        double AudToLkrRate = report.getAudToLkrRate();

        // Table 02 Row 01
        table.addCell(new Cell(1, 2).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 2).add(new Paragraph("TOP PERFORMING SONG SUMMARY").setFont(FONT_RUBIK_SEMIBOLD))
                .setVerticalAlignment(VerticalAlignment.BOTTOM).setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10f).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 2).add(new Paragraph("")).setBorder(Border.NO_BORDER));

        table.addCell(new Cell().add(new Paragraph("SONG").setFont(FONT_RUBIK_SEMIBOLD).setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER).setBackgroundColor(INVOICE_BLUE));
        table.addCell(new Cell().add(new Paragraph("AMOUNT").setFont(FONT_RUBIK_SEMIBOLD).setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER))
                .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER).setBackgroundColor(INVOICE_BLUE));

        for (int i = 0; i <= (songCount -1); i++) {
            Songs song = topPerformingSongs.get(i);

            String trackTitle = song.getTrackTitle();

            double royalty = song.getRoyalty() * eurToAudRate * AudToLkrRate;
            String finalAmount = "LKR " + String.format("%,9.2f", royalty) + "/=";

            table.addCell(new Cell().add(new Paragraph(trackTitle).setFont(FONT_POPPINS))
                    .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
            table.addCell(new Cell().add(new Paragraph(finalAmount).setFont(FONT_POPPINS))
                    .setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f).setFontSize(10f).setBorder(BLUE_BORDER));
        }

        return table;
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
        double conversionRate = report.getEurToAudRate();
        List<CoWriterShare> coWriterShares = report.getCoWritterList();

        // Table
        float[] columnWidth = {200f, 50f, 50f, 200f, 100f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);

        // Table 02 Row 01
        table.addCell(new Cell(1, 5).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 5).add(new Paragraph("CO-WRITER PAYMENT SUMMARY").setFont(FONT_RUBIK_SEMIBOLD))
                .setVerticalAlignment(VerticalAlignment.BOTTOM).setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10f).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 5).add(new Paragraph("")).setBorder(Border.NO_BORDER));

        // Table 02 Row 02
        table.addCell(new Cell().add(new Paragraph("Song").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(INVOICE_BLUE).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph("Type").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(INVOICE_BLUE).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph("Share").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(INVOICE_BLUE).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph("Contributor").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(INVOICE_BLUE).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph("Amount").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(INVOICE_BLUE).setFontSize(10f).setBorder(BLUE_BORDER));

        for (CoWriterShare share : coWriterShares) {
            double royalty = share.getRoyalty() * conversionRate;
            table.addCell(new Cell().add(new Paragraph(share.getSongName()).setFont(FONT_POPPINS))
                    .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
            table.addCell(new Cell().add(new Paragraph(share.getSongType()).setFont(FONT_POPPINS))
                    .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
            table.addCell(new Cell().add(new Paragraph(share.getShare()).setFont(FONT_POPPINS))
                    .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
            table.addCell(new Cell().add(new Paragraph(share.getContributor()).setFont(FONT_POPPINS))
                    .setPaddingLeft(10f).setFontSize(10f).setBorder(BLUE_BORDER));
            table.addCell(new Cell().add(new Paragraph("LKR " + String.format("%,09.2f", royalty)).setFont(FONT_POPPINS))
                    .setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f).setFontSize(10f).setBorder(BLUE_BORDER));
            // "LKR " + String.format("%,9.2f", royalty) + "/="
        }

        return table;
    }

    private Table getTable03(ArtistReport report) {
        // Table
        float[] columnWidth = {200f, 5f, 200f, 5f, 200f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);
        DecimalFormat df = new DecimalFormat("0.00");

        // Row 01
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Gross Revenue Produced").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(10f).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBackgroundColor(INVOICE_BLUE).setBorder(BLUE_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph(""))
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Partner Share of Gross").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(10f).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(INVOICE_DARK_BLUE).setBorder(DARK_BLUE_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Amount Payable").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(10f).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(INVOICE_DARK_BLUE).setBorder(DARK_BLUE_BORDER));

        // Row 02
        table.addCell(new Cell().add(new Paragraph("AUD " + String.format("%,9.2f", report.getGrossRevenueInAUD())).setFont(FONT_POPPINS))
                .setPaddingLeft(5f).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER).setFontSize(10f).setBorder(BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph(""))
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("AUD " + String.format("%,9.2f", report.getPartnerShareInAUD())).setFont(FONT_POPPINS))
                .setFontSize(10f).setTextAlignment(TextAlignment.CENTER).setBorder(DARK_BLUE_BORDER));
        table.addCell(new Cell().add(new Paragraph(""))
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("LKR " + String.format("%,9.2f", report.getPartnerShareInLKR())).setFont(FONT_POPPINS))
                .setFontSize(10f).setTextAlignment(TextAlignment.CENTER).setBorder(DARK_BLUE_BORDER));

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
        table.addCell(new Cell().add(new Paragraph(report.getPayee() + " - " + report.getMonth())
                        .setFontSize(16f)
                        .setFont(FONT_RUBIK_SEMIBOLD)
                        .setFontColor(INVOICE_BLACK))
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
        /*table.addCell(new Cell()
                .add(new Paragraph(""))
                .setBackgroundColor(INVOICE_LIGHT_BLUE)
                .setBorder(Border.NO_BORDER)
        );*/
        //</editor-fold>
        return table;
    }

    public static String getSaveLocation(Window window) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        chooser.setTitle("Save As");
        File pathTo = chooser.showSaveDialog(window);
        return pathTo.getAbsolutePath();
    }

    public Document generateReport(String path, ArtistReport report) throws IOException {
        PDFDocument pdfDocument = new PDFDocument();
        Document document = pdfDocument.getDocument(path);

        // Images
        Image reportHeading = loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png");

        // Fonts
        FONT_RUBIK_SEMIBOLD = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Rubik-SemiBold.ttf");
        FONT_POPPINS = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Poppins-Regular.ttf");

        // Table 01
        Table tableHeader = getHeaderTable(reportHeading);
        Table tableFooter = getFooterTable();
        tableFooter.setFixedPosition(20f, document.getBottomMargin(), document.getWidth());
        Table table02 = getTable02(report);
        Table table03 = getTable03(report);
        Table tableTopPerformingSongsSummary = getTopPerformingSongsTable(report);
        Table tableCoWriterPaymentSummary = getCoWriterSummaryTable(report);
        Table tableCoWriterPayments = getCoWriterPaymentsTable(report);

        document.add(tableHeader); // Letter Head
        document.add(table02); // Artist Name and Month
        document.add(table03);
        // document.add(table04);
        document.add(tableCoWriterPaymentSummary); // Co-Writer Payment Summary
        document.add(tableTopPerformingSongsSummary); // Co-Writer Payment Summary
        document.add(tableFooter);

        document.add(new AreaBreak());

        document.add(tableHeader); // Letter Head
        document.add(table02); // Artist Name and Month
        document.add(tableCoWriterPayments);

        document.close();

        return document;
    }

    private Table getCoWriterPaymentsTable(ArtistReport report) {
        // Table
        float[] columnWidth = {200f, 50f, 50f, 100f, 200f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);

        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Title").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(10f).setTextAlignment(TextAlignment.CENTER).
                setVerticalAlignment(VerticalAlignment.MIDDLE).setBackgroundColor(INVOICE_BLUE).setBorder(BLUE_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(10f).setTextAlignment(TextAlignment.CENTER).
                setVerticalAlignment(VerticalAlignment.MIDDLE).setBackgroundColor(INVOICE_BLUE).setBorder(BLUE_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(10f).setTextAlignment(TextAlignment.CENTER).
                setVerticalAlignment(VerticalAlignment.MIDDLE).setBackgroundColor(INVOICE_BLUE).setBorder(BLUE_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Artist Share").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(10f).setTextAlignment(TextAlignment.CENTER).
                setVerticalAlignment(VerticalAlignment.MIDDLE).setBackgroundColor(INVOICE_BLUE).setBorder(BLUE_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Co-Writer").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_WHITE).setFontSize(10f).setTextAlignment(TextAlignment.CENTER).
                setVerticalAlignment(VerticalAlignment.MIDDLE).setBackgroundColor(INVOICE_BLUE).setBorder(BLUE_BORDER));

        List<CoWriterShare> coWriterShares = report.getCoWritterList();
        double eurToAudRate = report.getEurToAudRate();
        double audToLkrRate = report.getAudToLkrRate();

        for (CoWriterShare share : coWriterShares) {
            String songName = share.getSongName();
            String songType = share.getSongType();
            String percentage = share.getShare();
            double artistShare = share.getRoyalty();
            String coWriter = share.getContributor();

            table.addCell(new Cell().add(new Paragraph(songName).setFont(FONT_POPPINS).setPaddingLeft(5f))
                    .setPaddingLeft(5f).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.LEFT).setFontSize(10f).setBorder(BLUE_BORDER));
            table.addCell(new Cell().add(new Paragraph(songType).setFont(FONT_POPPINS))
                    .setPaddingLeft(5f).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER).setFontSize(10f).setBorder(BLUE_BORDER));
            table.addCell(new Cell().add(new Paragraph(percentage).setFont(FONT_POPPINS))
                    .setPaddingLeft(5f).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER).setFontSize(10f).setBorder(BLUE_BORDER));
            table.addCell(new Cell().add(new Paragraph("LKR " + String.format("%,9.2f", artistShare * eurToAudRate * audToLkrRate) + "/=").setFont(FONT_POPPINS))
                    .setPaddingLeft(5f).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER).setFontSize(10f).setBorder(BLUE_BORDER));
            table.addCell(new Cell().add(new Paragraph(coWriter).setFont(FONT_POPPINS).setPaddingLeft(5f))
                    .setPaddingLeft(5f).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.LEFT).setFontSize(10f).setBorder(BLUE_BORDER));

            // System.out.println(songName + " | " + songType + " | " + percentage + " | " + artistShare + " | " + coWriter);
        }

        return table;
    }
}
