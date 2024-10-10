package com.example.song_finder_fx.Model;

import com.example.song_finder_fx.Constants.Colors;
import com.example.song_finder_fx.Controller.ItemSwitcher;
import com.example.song_finder_fx.Controller.PDFDocument;
import com.example.song_finder_fx.Main;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import static com.google.common.io.Files.getFileExtension;

public class ReportPDFNew implements Colors {
    private static PdfFont FONT_POPPINS_SEMIBOLD = null;
    private static PdfFont FONT_RUBIK_SEMIBOLD = null;
    private static PdfFont FONT_POPPINS = null;
    private static PdfFont FONT_POPPINS_MEDIUM = null;
    private static int srRowCount = 0;
    private static int pubRowCount = 0;
    private String reportPath;
    private static final int PAGE01 = 1;
    private static final int PAGE02 = 2;

    public void generateReport(String path, ArtistReport report) throws IOException, SQLException, ClassNotFoundException {
        // Create document
        PDFDocument pdfDocument = new PDFDocument();
        Document document = pdfDocument.getDocument(path);

        setBackgroundColor(document);

        // Images
        System.out.println("Getting Report Header Images...");
        Image reportHeading = getArtistHeading(report.getArtist().getName(), PAGE01);
        Image reportHeadingSmall = getArtistHeading(report.getArtist().getName(), PAGE02);
        Image reportFooter = new Image(ImageDataFactory.create("src/main/resources/com/example/song_finder_fx/images/reports/revenue_report_footer.png"));
        reportFooter.setAutoScale(true);

        // Fonts
        FONT_RUBIK_SEMIBOLD = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Rubik-SemiBold.ttf");
        FONT_POPPINS = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Poppins-Regular.ttf");
        FONT_POPPINS_MEDIUM = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Poppins-Medium.ttf");
        FONT_POPPINS_SEMIBOLD = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Poppins-SemiBold.ttf");

        // Add text on top of the letter head
        Paragraph reportMonthAndYear = new Paragraph(report.getMonth().toUpperCase() + " " + report.getYear())
                .setFont(FONT_RUBIK_SEMIBOLD)
                .setFontSize(20)
                .setFontColor(INVOICE_BLUE);
        reportMonthAndYear.setFixedPosition(28, 760, 600);

        // Tables
        Table tableHeader = getHeaderTable(reportHeading);
        Table reportSummary = getReportSummaryTable(report);
        reportSummary.setFixedPosition(25, 570, 540);
        Table srBreakdown = getSRBreakdownTable(report);
        srBreakdown.setWidth(540f);

        // srBreakdown.getNumberOfRows();

        Table pubBreakdown = null;
        if (!report.getPUBAssetBreakdown().isEmpty()) {
            pubBreakdown = getPubBreakdownTable(report);
            pubBreakdown.setWidth(540f);
        }

        Table streamingBreakdown = getStreamingBreakdownTable(report);
        streamingBreakdown.setWidth(540f);
        Table territoryBreakdown = getTerritoryBreakdownTable(report);
        territoryBreakdown.setWidth(540f);
        Table reportFooterTable = getFooterTable(reportFooter);
        reportFooterTable.setFixedPosition(0, 0, 600);


        document.add(tableHeader); // Letter Head
        document.add(reportMonthAndYear); // Report Month and Year
        document.add(reportSummary);
        document.add(srBreakdown);

        // Page 01 /////////////////////////////////////////////////////////////////////////////////////////////////////
        int srRows = srRowCount;
        int pubRows = pubRowCount;
        int remainingRowsOnFirstPage = 10 - srRows;
        if (pubBreakdown != null) {
            if (srRows <= 10 && (pubRows <= remainingRowsOnFirstPage)) {
                // Add pubBreakdown on the same page
                document.add(pubBreakdown);
            } else {
                // Add a new page and the small header before adding pubBreakdown
                document.add(new AreaBreak());
                document.add(reportHeadingSmall);
                document.add(pubBreakdown);
            }
        }

        // Page 02 /////////////////////////////////////////////////////////////////////////////////////////////////////
        document.add(new AreaBreak());

        document.add(reportHeadingSmall);
        document.add(streamingBreakdown);

        // Page 03 /////////////////////////////////////////////////////////////////////////////////////////////////////
        document.add(new AreaBreak());

        document.add(reportHeadingSmall);
        document.add(territoryBreakdown);
        document.add(reportFooterTable);

        document.close();

        this.reportPath = path;
    }

    private Table getPubBreakdownTable(ArtistReport report) {
        // Table
        float[] columnWidth = {50f, 200f, 50f, 50f, 125f, 125f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        PdfFont subtitleFont = FONT_POPPINS_MEDIUM;
        TextAlignment textAlignment = TextAlignment.CENTER;
        Border border = Border.NO_BORDER;

        // Heading
        table.addCell(new Cell(1, 6).setBorder(border).add(new Paragraph("")));
        table.addCell(new Cell(1, 6).setBorder(border).add(new Paragraph("")));
        table.addCell(new Cell(1, 6).setBorder(border).add(new Paragraph("")));
        table.addCell(new Cell(1, 6)
                .setBorder(border)
                .add(new Paragraph("Publishing Breakdown")
                        .setFont(FONT_POPPINS_SEMIBOLD)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontColor(INVOICE_BLUE)
                        .setFontSize(15f)
                )
        );

        // Row 01
        table.addCell(new Cell().add(new Paragraph("")
                .setFont(subtitleFont)).setBorder(border));
        table.addCell(new Cell().add(new Paragraph("")
                .setFont(subtitleFont)).setBorder(border));
        table.addCell(new Cell().add(new Paragraph("Splits")
                        .setFont(subtitleFont)
                        .setTextAlignment(textAlignment))
                .setFontSize(10f).setBorder(border));
        // table.addCell(new Cell().setHeight(20f).add(image).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(new Paragraph("Tracks")
                        .setFont(subtitleFont)
                        .setTextAlignment(textAlignment))
                .setFontSize(10f).setBorder(border));
        table.addCell(new Cell().add(new Paragraph("Artist Share (AUD)")
                        .setFont(subtitleFont)
                        .setTextAlignment(textAlignment))
                .setFontSize(10f).setBorder(border));
        // table.addCell(new Cell().setHeight(20f).add(image).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(new Paragraph("Artist Share (LKR)")
                        .setFont(subtitleFont)
                        .setTextAlignment(textAlignment))
                .setFontSize(10f).setBorder(border));


        List<CoWriterShare> assetBreakdown = report.getPUBAssetBreakdown();
        double eurToAUD_Rate = report.getEurToAudRate();
        double audToLKR_Rate = report.getAudToLkrRate();

        // Create a Map to group songs by name
        Map<String, List<CoWriterShare>> groupedAssets = new HashMap<>();

        // Group assets by UPC
        System.out.println("Grouping assets by song name...");
        for (CoWriterShare asset : assetBreakdown) {
            groupedAssets.computeIfAbsent(asset.getSongName(), k -> new ArrayList<>()).add(asset);
        }

        // Create a list of grouped entries for sorting
        List<Map.Entry<String, List<CoWriterShare>>> sortedEntries = new ArrayList<>(groupedAssets.entrySet());

        // Sort the entries based on total royalty in descending order
        sortedEntries.sort((e1, e2) -> {
            double totalRoyalty1 = e1.getValue().stream().mapToDouble(CoWriterShare::getRoyalty).sum();
            double totalRoyalty2 = e2.getValue().stream().mapToDouble(CoWriterShare::getRoyalty).sum();
            return Double.compare(totalRoyalty2, totalRoyalty1); // Descending order
        });

        // Process grouped assets
        for (Map.Entry<String, List<CoWriterShare>> entry : sortedEntries) {
            String upc = entry.getKey();
            List<CoWriterShare> assets = entry.getValue();
            String productTitle = upc;
            if (assets.getFirst().getProductTitle() != null) {
                productTitle = assets.getFirst().getProductTitle();
                System.out.println("Changed product title to: " + productTitle);
            }

            // Calculate combined values
            int trackCount = assets.size();
            double totalRoyaltyEUR = assets.stream().mapToDouble(CoWriterShare::getRoyalty).sum();
            double totalRoyaltyAUD = totalRoyaltyEUR / eurToAUD_Rate;
            double totalRoyaltyLKR = totalRoyaltyAUD * audToLKR_Rate;

            // Get splits (assuming all entries for the same song have the same split)
            String splits = assets.getFirst().getShare();

            // Format values
            String tracks = String.valueOf(trackCount);
            String artistShareAUD = String.format("%,9.2f", totalRoyaltyAUD);
            String artistShareLKR = String.format("%,9.2f", totalRoyaltyLKR);

            // Get song image for "SR" type asset if available
            // Image songImage = loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/logo_small_50x.png", true);;

            // Add row to the table
            addSongSummaryRowPUB(table, productTitle, splits, tracks, artistShareAUD, artistShareLKR);
        }


        return table;
    }

    private Table getFooterTable(Image reportFooter) {
        float[] columnWidth = {1000f};
        Table table = new Table(columnWidth);

        table.addCell(new Cell().add(reportFooter).setBorder(Border.NO_BORDER).setMargin(0f));

        return table;
    }

    private Table getStreamingBreakdownTable(ArtistReport report) throws IOException {
        float[] columnWidth = {50f, 300f, 125f, 125f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        float fontSizeSubTitle = 10f;
        PdfFont titleFont = FONT_POPPINS_SEMIBOLD;
        PdfFont subtitleFont = FONT_POPPINS_MEDIUM;
        VerticalAlignment verticalAlignment = VerticalAlignment.MIDDLE;
        Border border = Border.NO_BORDER;

        // Heading
        table.addCell(new Cell(1, 4).setBorder(border).add(new Paragraph("")));
        table.addCell(new Cell(1, 4).setBorder(border).add(new Paragraph("")));
        table.addCell(new Cell(1, 4).setBorder(border).add(new Paragraph("")));
        table.addCell(new Cell(1, 4)
                .setBorder(border)
                .add(new Paragraph("Streaming Breakdown")
                        .setFont(titleFont)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontColor(INVOICE_BLUE)
                        .setFontSize(15f)
                )
        );

        // Header
        // Values
        table.addCell(new Cell(1, 2).setHeight(30f).setBorder(border).add(new Paragraph("").setFont(subtitleFont).setTextAlignment(TextAlignment.LEFT).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("Amount (LKR)").setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("Streams").setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));

        // Cell cell = new Cell().setBackgroundColor(backgroundColor).setHeight(30f).setBorder(border).add(new Image(ImageDataFactory.create("src/main/resources/com/example/song_finder_fx/images/spotify.png")).setAutoScale(true));

        List<DSPBreakdown> dspBreakdown = report.getDSPBreakdown();
        double eurToAudRate = report.getEurToAudRate();
        double audToLkrRate = report.getAudToLkrRate();

        for (DSPBreakdown dsp : dspBreakdown) {
            String dspName = dsp.dsp();
            Image dspImage = getDSPImage(dsp.dsp());
            double reportedRoyalty = dsp.reportedRoyaltyForCEYMusic();
            int assetQuantity = dsp.assetQuantity();

            double processedRoyalty = reportedRoyalty / eurToAudRate * audToLkrRate;

            // Values
            table.addCell(new Cell().setHeight(30f).setBorder(border).add(dspImage));
            table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph(dspName).setFont(subtitleFont).setTextAlignment(TextAlignment.LEFT).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
            table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph(String.format("%,9.2f", processedRoyalty)).setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
            table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph(String.valueOf(assetQuantity)).setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));

            table.addCell(new Cell(1, 4).setBorder(border).add(new Paragraph("")));
        }

        return table;
    }

    private Table getTerritoryBreakdownTable(ArtistReport report) throws IOException {
        float[] columnWidth = {50f, 300f, 125f, 125f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        float fontSizeSubTitle = 10f;
        PdfFont titleFont = FONT_POPPINS_SEMIBOLD;
        PdfFont subtitleFont = FONT_POPPINS_MEDIUM;
        VerticalAlignment verticalAlignment = VerticalAlignment.MIDDLE;
        Border border = Border.NO_BORDER;

        // Heading
        table.addCell(new Cell(1, 4).setBorder(border).add(new Paragraph("")));
        table.addCell(new Cell(1, 4).setBorder(border).add(new Paragraph("")));
        table.addCell(new Cell(1, 4).setBorder(border).add(new Paragraph("")));
        table.addCell(new Cell(1, 4)
                .setBorder(border)
                .add(new Paragraph("Territory Breakdown")
                        .setFont(titleFont)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontColor(INVOICE_BLUE)
                        .setFontSize(15f)
                )
        );

        // Header
        // Values
        table.addCell(new Cell(1, 2).setHeight(30f).setBorder(border).add(new Paragraph("").setFont(subtitleFont).setTextAlignment(TextAlignment.LEFT).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("Amount (LKR)").setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("Streams").setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));

        // Cell cell = new Cell().setBackgroundColor(backgroundColor).setHeight(30f).setBorder(border).add(new Image(ImageDataFactory.create("src/main/resources/com/example/song_finder_fx/images/spotify.png")).setAutoScale(true));

        List<TerritoryBreakdown> territoryBreakdowns = report.getTerritoryBreakdown();
        double eurToAudRate = report.getEurToAudRate();
        double audToLkrRate = report.getAudToLkrRate();

        int rowCount = 0;
        double othersTotalRoyalty = 0;
        int othersTotalAssetQuantity = 0;

        for (TerritoryBreakdown territoryBreakdown : territoryBreakdowns) {
            if (rowCount < 12) {
                String territory = territoryBreakdown.territory();
                System.out.println("territory = " + territory);
                Image territoryImage = getTerritoryImage(territoryBreakdown.territory());
                double reportedRoyalty = territoryBreakdown.reportedRoyaltyForCEYMusic();
                int assetQuantity = territoryBreakdown.assetQuantity();

                double processedRoyalty = reportedRoyalty / eurToAudRate * audToLkrRate;

                // Values
                table.addCell(new Cell().setHeight(30f).setBorder(border).add(territoryImage));
                table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph(ItemSwitcher.setTerritoryName(territory)).setFont(subtitleFont).setTextAlignment(TextAlignment.LEFT).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
                table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph(String.format("%,9.2f", processedRoyalty)).setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
                table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph(String.valueOf(assetQuantity)).setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));

                table.addCell(new Cell(1, 4).setBorder(border).add(new Paragraph("")));

                rowCount++;
            } else {
                double reportedRoyalty = territoryBreakdown.reportedRoyaltyForCEYMusic();
                double processedRoyalty = reportedRoyalty / eurToAudRate * audToLkrRate;
                othersTotalRoyalty += processedRoyalty;
                othersTotalAssetQuantity += territoryBreakdown.assetQuantity();
            }
        }

        // Add "Others" row if there are more than 12 territories
        if (rowCount >= 12 && othersTotalRoyalty > 0) {
            table.addCell(new Cell().setHeight(30f).setBorder(border).add(getTerritoryImage("Others")));
            table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("Others").setFont(subtitleFont).setTextAlignment(TextAlignment.LEFT).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
            table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph(String.format("%,9.2f", othersTotalRoyalty)).setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
            table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph(String.valueOf(othersTotalAssetQuantity)).setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));

            table.addCell(new Cell(1, 4).setBorder(border).add(new Paragraph("")));
        }

        return table;
    }

    private Image getTerritoryImage(String territory) throws IOException {
        return switch (territory) {
            case "LK" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_sri_lanka.png");
            case "AU" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_australia.png");
            case "US" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_united_states.png");
            case "GB" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_united_kingdom.png");
            case "CA" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_canada.png");
            case "JP" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_japan.png");
            case "AE" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_uae.png");
            case "IT" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_italy.png");
            case "NZ" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_new_zealand.png");
            case "TH" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_thailand.png");
            case "DE" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_germany.png");
            case "KR" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_korea.png");
            case "ID" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_indonesia.png");
            case "MY" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_malaysia.png");
            case "PH" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_philippines.png");
            case "FR" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_france.png");
            case "BR" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_brazil.png");
            case "KW" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_kuwait.png");
            case "RO" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_romania.png");
            case "SG" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_singapore.png");
            case "SA" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_saudi_arabia.png");
            case "IN" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_india.png");
            case "SE" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_sweden.png");
            default ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/reports/territory_flags/flag_default.png");
        };
    }

    private Image getDSPImage(String dsp) throws IOException {
        return switch (dsp) {
            case "Spotify" -> loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/spotify.png");
            case "Apple Music", "iTunes Match", "iTunes" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/itunes.png");
            case "Youtube Music" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/ytmusic.png");
            case "Youtube Ad Supported" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/yt_square.png");
            case "TikTok" -> loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/tiktok.png");
            case "Facebook Audio Library", "Facebook Fingerprinting" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/fb.png");
            case "Soundcloud" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/dsp_soundcloud.png");
            case "Amazon Unlimited", "Amazon Prime", "Amazon ADS" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/dsp_amazon.png");
            case "Snap" -> loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/dsp_snapchat.png");
            case "Hungama" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/dsp_hungama.png");
            case "Tidal" -> loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/dsp_tidal.png");
            case "Deezer" -> loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/dsp_deezer.png");
            case "LINE Music" ->
                    loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/dsp_line_music.png");

            default -> loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/logo_small_200x.png");
        };
    }

    private static void setBackgroundColor(Document document) {
        Table tableColored = new Table(new float[]{600f});
        tableColored.addCell(new Cell()
                .setBackgroundColor(new DeviceRgb(245, 247, 250))
                .setBorder(Border.NO_BORDER)
                .setHeight(PageSize.A4.getHeight()));
        tableColored.setFixedPosition(0, 0, PageSize.A4.getWidth());
        document.add(tableColored);
    }

    private Table getSRBreakdownTable(ArtistReport report) throws IOException, SQLException, ClassNotFoundException {
        // Table
        float[] columnWidth = {50f, 200f, 50f, 50f, 125f, 125f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        PdfFont subtitleFont = FONT_POPPINS_MEDIUM;
        TextAlignment textAlignment = TextAlignment.CENTER;
        Border border = Border.NO_BORDER;

        // Row 01
        table.addCell(new Cell().add(new Paragraph("")
                .setFont(subtitleFont)).setBorder(border));
        table.addCell(new Cell().add(new Paragraph("")
                .setFont(subtitleFont)).setBorder(border));
        table.addCell(new Cell().add(new Paragraph("Splits")
                        .setFont(subtitleFont)
                        .setTextAlignment(textAlignment))
                .setFontSize(10f).setBorder(border));
        // table.addCell(new Cell().setHeight(20f).add(image).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(new Paragraph("Tracks")
                        .setFont(subtitleFont)
                        .setTextAlignment(textAlignment))
                .setFontSize(10f).setBorder(border));
        table.addCell(new Cell().add(new Paragraph("Artist Share (AUD)")
                        .setFont(subtitleFont)
                        .setTextAlignment(textAlignment))
                .setFontSize(10f).setBorder(border));
        // table.addCell(new Cell().setHeight(20f).add(image).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(new Paragraph("Artist Share (LKR)")
                        .setFont(subtitleFont)
                        .setTextAlignment(textAlignment))
                .setFontSize(10f).setBorder(border));


        List<CoWriterShare> assetBreakdown = report.getSRAssetBreakdown();
        double eurToAUD_Rate = report.getEurToAudRate();
        double audToLKR_Rate = report.getAudToLkrRate();

        // Create a Map to group songs by name
        Map<String, List<CoWriterShare>> groupedAssets = new HashMap<>();

        // Group assets by UPC
        System.out.println("Grouping assets by song name...");
        for (CoWriterShare asset : assetBreakdown) {
            groupedAssets.computeIfAbsent(asset.getUpc(), k -> new ArrayList<>()).add(asset);
        }

        // Create a list of grouped entries for sorting
        List<Map.Entry<String, List<CoWriterShare>>> sortedEntries = new ArrayList<>(groupedAssets.entrySet());

        // Sort the entries based on total royalty in descending order
        sortedEntries.sort((e1, e2) -> {
            double totalRoyalty1 = e1.getValue().stream().mapToDouble(CoWriterShare::getRoyalty).sum();
            double totalRoyalty2 = e2.getValue().stream().mapToDouble(CoWriterShare::getRoyalty).sum();
            return Double.compare(totalRoyalty2, totalRoyalty1); // Descending order
        });

        // Process grouped assets
        for (Map.Entry<String, List<CoWriterShare>> entry : sortedEntries) {
            String upc = entry.getKey();
            List<CoWriterShare> assets = entry.getValue();
            String productTitle = upc;
            if (assets.getFirst().getProductTitle() != null) {
                productTitle = assets.getFirst().getProductTitle();
                System.out.println("Changed product title to: " + productTitle);
            }

            // Calculate combined values
            int trackCount = assets.size();
            double totalRoyaltyEUR = assets.stream().mapToDouble(CoWriterShare::getRoyalty).sum();
            double totalRoyaltyAUD = totalRoyaltyEUR / eurToAUD_Rate;
            double totalRoyaltyLKR = totalRoyaltyAUD * audToLKR_Rate;

            // Get splits (assuming all entries for the same song have the same split)
            String splits = assets.getFirst().getShare();

            // Format values
            String tracks = String.valueOf(trackCount);
            String artistShareAUD = String.format("%,9.2f", totalRoyaltyAUD);
            String artistShareLKR = String.format("%,9.2f", totalRoyaltyLKR);

            // Get song image for "SR" type asset if available
            Image songImage = getSongImage(assets.getFirst().getIsrc(), upc);

            // Add row to the table
            addSongSummaryRow(table, songImage, productTitle, splits, tracks, artistShareAUD, artistShareLKR);
        }


        return table;
    }

    private Image getSongImage(String isrc, String upc) throws IOException, SQLException, ClassNotFoundException {
        // TODO: Fetch Image for ISRC
        // Senanga, Abhisheka, Sangeeth Wijesuriya, WAYO, Ridma
        // SongSearch search = new SongSearch();
        // List<Songs> songList = search.searchSong(isrc, SearchType.ISRC, true);
        // String upc = songList.getFirst().getUPC();
        System.out.println("UPC: " + upc + " | ISRC: " + isrc);
        String searchLocation = Main.getAudioDatabaseLocation();
        String imagePath = findUPCImage(searchLocation, upc);

        System.out.println("Image Search Location: " + searchLocation);

        return loadAutoScaledImage(Objects.requireNonNullElse(imagePath, "src/main/resources/com/example/song_finder_fx/images/manual_claims/upload_artwork_90.jpg"));
        // String location = "src/main/resources/com/example/song_finder_fx/images/manual_claims/upload_artwork_90.jpg";
    }

    public static String findUPCImage(String searchLocation, String upc) throws IOException {
        // Create a File object for the search location
        File searchDir = new File(searchLocation);

        // Find the UPC folder
        File upcFolder = null;

        try {
            upcFolder = findUPCFolder(searchDir, upc);
        } catch (Exception e) {
            System.out.println("Cannot find UPC Folder: " + e.getMessage());
        }

        if (upcFolder == null) {
            return null;
        }

        // Find the UPC image file
        File upcImage = findUPCImageFile(upcFolder, upc);
        if (upcImage == null) {
            return null;
        }

        // Return the absolute path of the image file
        return upcImage.getAbsolutePath();
    }

    private static File findUPCFolder(File searchDir, String upc) {
        // Previous method which only checks if the folder name starts with the UPC
        /*File[] folders = searchDir.listFiles((dir, name) -> name.toLowerCase().startsWith(upc.toLowerCase()) && new File(dir, name).isDirectory());
        return (folders != null && folders.length > 0) ? folders[0] : null;*/

        // Modification to the method to check if any part of the folder name matches the UPC
        File[] folders = searchDir.listFiles((dir, name) -> {
            // Split the name by common separators
            String[] parts = name.split("[-_\\s]+");
            // Check if any part matches the UPC
            for (String part : parts) {
                if (part.trim().equalsIgnoreCase(upc)) {
                    return new File(dir, name).isDirectory();
                }
            }
            return false;
        });

        return (folders != null && folders.length > 0) ? folders[0] : null;
    }

    private static File findUPCImageFile(File upcFolder, String upc) throws IOException {
        /*File[] imageFiles = upcFolder.listFiles((dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return (lowercaseName.startsWith(upc.toLowerCase()) &&
                    (lowercaseName.endsWith(".png") || lowercaseName.endsWith(".jpg") || lowercaseName.endsWith(".jpeg")));
        });

        return (imageFiles != null && imageFiles.length > 0) ? imageFiles[0] : null;*/

        String miniFileName = upc.toLowerCase() + "_mini";
        File miniFile = findFile(upcFolder, miniFileName);

        if (miniFile != null) {
            System.out.println("Found Mini File for " + upc + ": " + miniFile.getAbsolutePath());
            return miniFile;
        }

        // If mini file doesn't exist, look for the original file
        File originalFile = findFile(upcFolder, upc.toLowerCase());

        if (originalFile != null) {
            // Resize the original file and save as mini
            System.out.println("Mini File not found for " + upc + ", resizing and saving as " + miniFileName);
            return resizeAndSaveMini(originalFile, upcFolder, miniFileName);
        }

        return null;
    }

    private static File resizeAndSaveMini(File originalFile, File folder, String miniFileName) throws IOException {
        BufferedImage originalImage = ImageIO.read(originalFile);

        // Define your desired dimensions for the mini image
        int targetWidth = 300;  // Example value, adjust as needed
        int targetHeight = 200; // Example value, adjust as needed

        // Calculate scaling factors
        double widthScale = (double) targetWidth / originalImage.getWidth();
        double heightScale = (double) targetHeight / originalImage.getHeight();
        double scale = Math.min(widthScale, heightScale);

        int scaledWidth = (int) (originalImage.getWidth() * scale);
        int scaledHeight = (int) (originalImage.getHeight() * scale);

        BufferedImage resizedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.getGraphics().drawImage(originalImage.getScaledInstance(scaledWidth, scaledHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);

        String fileExtension = getFileExtension(originalFile.getName());
        File miniFile = new File(folder, miniFileName + "." + fileExtension);
        ImageIO.write(resizedImage, fileExtension, miniFile);

        return miniFile;
    }

    private static File findFile(File folder, String baseFileName) {
        File[] matchingFiles = folder.listFiles((dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.startsWith(baseFileName) &&
                    (lowercaseName.endsWith(".png") || lowercaseName.endsWith(".jpg") || lowercaseName.endsWith(".jpeg"));
        });

        return (matchingFiles != null && matchingFiles.length > 0) ? matchingFiles[0] : null;
    }

    private static void addSongSummaryRow(Table table, Image songImage, String songName, String splits, String tracks, String artistShareAUD, String artistShareLKR) {
        VerticalAlignment verticalAlignment = VerticalAlignment.MIDDLE;
        Color backgroundColor = new DeviceRgb(226, 229, 233);
        PdfFont subtitleFont = FONT_POPPINS;
        TextAlignment textAlignment = TextAlignment.CENTER;
        Border border = Border.NO_BORDER;

        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));

        // Songs
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(songImage).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(Objects.requireNonNullElse(songName, "null")).setFont(FONT_POPPINS_MEDIUM)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(splits).setFont(subtitleFont).setTextAlignment(textAlignment)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(tracks).setFont(subtitleFont).setTextAlignment(textAlignment)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(artistShareAUD).setFont(subtitleFont).setTextAlignment(textAlignment)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(artistShareLKR).setFont(subtitleFont).setTextAlignment(textAlignment)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));

        srRowCount++;
    }

    private static void addSongSummaryRowPUB(Table table, String songName, String splits, String tracks, String artistShareAUD, String artistShareLKR) {
        VerticalAlignment verticalAlignment = VerticalAlignment.MIDDLE;
        Color backgroundColor = new DeviceRgb(226, 229, 233);
        PdfFont subtitleFont = FONT_POPPINS;
        TextAlignment textAlignment = TextAlignment.CENTER;
        Border border = Border.NO_BORDER;

        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));

        // Songs
        // table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(songImage).setBorder(border));
        table.addCell(new Cell(1, 2).setPaddingLeft(15f).setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(Objects.requireNonNullElse(songName, "null")).setFont(FONT_POPPINS_MEDIUM)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(splits).setFont(subtitleFont).setTextAlignment(textAlignment)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(tracks).setFont(subtitleFont).setTextAlignment(textAlignment)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(artistShareAUD).setFont(subtitleFont).setTextAlignment(textAlignment)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(artistShareLKR).setFont(subtitleFont).setTextAlignment(textAlignment)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));

        pubRowCount++;
    }

    private Table getReportSummaryTable(ArtistReport report) throws MalformedURLException {
        // Table
        float[] columnWidth = {5f, 200f, 5f, 5f, 200f, 5f, 5f, 200f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        float fontSizeSubTitle = 20f;
        float cellHeight = 42f;
        PdfFont subtitleFont = FONT_POPPINS_MEDIUM;

        Border border = new SolidBorder(INVOICE_BLUE, 1);

        Border.Side bottom = Border.Side.BOTTOM;

        // Image image = loadGrowIcon();
        // image.setWidth(10);

        // Row 01
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Gross Revenue Produced").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_DARK_BLUE).setFontSize(10f)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Partner Share of Gross").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_DARK_BLUE)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10f)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("")).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().setHeight(20f).add(new Paragraph("Amount Payable").setFont(FONT_RUBIK_SEMIBOLD))
                .setFontColor(INVOICE_DARK_BLUE)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10f)
                .setBorder(Border.NO_BORDER));

        // Row 02
        table.addCell(new Cell().setHeight(cellHeight).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell()
                        .setHeight(cellHeight)
                .add(new Paragraph("AUD " + String.format("%,.2f", report.getGrossRevenueInAUD())).setFont(subtitleFont))
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(fontSizeSubTitle)
                .setFontColor(INVOICE_DARK_BLUE)
                .setBorder(border));
        table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(cellHeight).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell()
                        .setHeight(cellHeight)
                .add(new Paragraph("AUD " + String.format("%,.2f", report.getPartnerShareInAUD())).setFont(subtitleFont))
                .setFontColor(INVOICE_DARK_BLUE)
                .setFontSize(fontSizeSubTitle)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(border));
        table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(cellHeight).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell()
                        .setHeight(cellHeight)
                .add(new Paragraph("LKR " + String.format("%,.2f", report.getPartnerShareInLKR())).setFont(subtitleFont))
                .setFontColor(INVOICE_DARK_BLUE)
                .setFontSize(fontSizeSubTitle)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(border));

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

    static Image getArtistHeading(String artistName, int pageNumber) throws MalformedURLException {
        Image invoiceHeading = new Image(ImageDataFactory.create(getArtistHeadingImage(artistName, pageNumber)));
        invoiceHeading.setAutoScale(true);
        return invoiceHeading;
    }

    static Image loadGrowIcon() throws MalformedURLException {
        Image image = new Image(ImageDataFactory.create("src/main/resources/com/example/song_finder_fx/images/reports/icon_grow.png"));
        image.setAutoScale(false);
        return image;
    }

    static Image loadAutoScaledImage(String location) throws IOException {
        Image image = new Image(ImageDataFactory.create(location));

        // Set the new dimensions
        image.setWidth(30f);
        image.setHeight(30f);

        image.setAutoScale(true);

        return image;
    }

    private static String getArtistHeadingImage(String artistName, int pageNumber) {
        switch (artistName) {
            case "Ridma Weerawardena" -> {
                if (pageNumber == 1)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/ridmaw.png";
                else if (pageNumber == 2)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/ridmaw_head_small.png";
                else return "src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png";
            }
            case "Methun SK" -> {
                if (pageNumber == 1)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/methunsk.png";
                else if (pageNumber == 2)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/methunsk_head_small.png";
                else return "src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png";
            }
            case "Abhisheka Wimalaweera" -> {
                if (pageNumber == 1)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/abhishekaw.png";
                else if (pageNumber == 2)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/abhishekaw_head_small.png";
                else return "src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png";
            }
            case "Aruna Lian" -> {
                if (pageNumber == 1)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/arunal.png";
                else if (pageNumber == 2)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/arunal_head_small.png";
                else return "src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png";
            }
            case "Sangeeth Wijesuriya" -> {
                if (pageNumber == 1)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/sangeethw.png";
                else if (pageNumber == 2)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/sangeethw_head_small.png";
                else return "src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png";
            }
            case "Senanga Dissanayake" -> {
                if (pageNumber == 1)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/senangad.png";
                else if (pageNumber == 2)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/senangad_head_small.png";
                else return "src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png";
            }
            case "WAYO" -> {
                if (pageNumber == 1)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/wayo.png";
                else if (pageNumber == 2)
                    return "src/main/resources/com/example/song_finder_fx/images/reports/artists/wayo_head_small.png";
                else return "src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png";
            }
            case null, default -> {
                return "src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png";
            }
        }
    }

    public String getReportPath() {
        return this.reportPath;
    }

}
