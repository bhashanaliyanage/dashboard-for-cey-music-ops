package com.example.song_finder_fx.Model;

import com.example.song_finder_fx.Constants.Colors;
import com.example.song_finder_fx.Constants.SearchType;
import com.example.song_finder_fx.Controller.ImageProcessor;
import com.example.song_finder_fx.Controller.PDFDocument;
import com.example.song_finder_fx.Main;
import com.example.song_finder_fx.Organizer.SongSearch;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ReportPDFNew implements Colors {
    private static PdfFont FONT_RUBIK_SEMIBOLD = null;
    private static PdfFont FONT_POPPINS = null;
    private static PdfFont FONT_POPPINS_MEDIUM = null;
    private static final Border DARK_BLUE_BORDER = new SolidBorder(INVOICE_DARK_BLUE, 0.5f);

    public void generateReport(String path, ArtistReport report) throws IOException, SQLException, ClassNotFoundException {
        PDFDocument pdfDocument = new PDFDocument();
        Document document = pdfDocument.getDocument(path);

        setBackgroundColor(document);

        // Images
        Image reportHeading = getArtistHeading(report.getArtist().getName());

        // Fonts
        FONT_RUBIK_SEMIBOLD = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Rubik-SemiBold.ttf");
        FONT_POPPINS = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Poppins-Regular.ttf");
        FONT_POPPINS_MEDIUM = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Poppins-Medium.ttf");

        if (FONT_POPPINS_MEDIUM == null) {
            System.out.println("Poppins Medium Null");
        }

        // Tables
        Table tableHeader = getHeaderTable(reportHeading);
        Table reportSummary = getReportSummaryTable(report);
        reportSummary.setFixedPosition(35, 570, 540);
        Table songBreakdown = getSongBreakdownTable(report);
        // songBreakdown.setFixedPosition(35, 500, 540);
        songBreakdown.setWidth(540f);

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
        document.add(songBreakdown);

        document.close();
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

    private Table getSongBreakdownTable(ArtistReport report) throws IOException, SQLException, ClassNotFoundException {
        // Table
        float[] columnWidth = {50f, 200f, 50f, 50f, 125f, 125f};
        Table table = new Table(columnWidth);
        table.setMarginLeft(20f);
        table.setMarginRight(20f);
        table.setMarginTop(10f);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        float fontSizeSubTitle = 15f;
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


        List<CoWriterShare> assetBreakdown = report.getAssetBreakdown();
        double audToLKR_Rate = report.getAudToLkrRate();

        for (CoWriterShare asset : assetBreakdown) {

            String songName = asset.getSongName();
            String splits = asset.getShare();
            String tracks = "1";
            String artistShareAUD = String.format("%,9.2f", asset.getRoyalty());
            String artistShareLKR = String.format("%,9.2f", asset.getRoyalty() * audToLKR_Rate);
            Image songImage = getSongImage(asset.getIsrc());

            addSongSummaryRow(table, songImage, songName, splits, tracks, artistShareAUD, artistShareLKR);

        }

        return table;
    }

    private Image getSongImage(String isrc) throws IOException, SQLException, ClassNotFoundException {
        // TODO: Fetch Image for ISRC
        // Senanga, Abhisheka, Sangeeth Wijesuriya, WAYO, Ridma
        SongSearch search = new SongSearch();
        List<Songs> songList = search.searchSong(isrc, SearchType.ISRC, true);
        String upc = songList.getFirst().getUPC();
        System.out.println("upc = " + upc);
        String searchLocation = Main.getAudioDatabaseLocation();
        String imagePath = findUPCImage(searchLocation, upc);

        // System.out.println("imagePath = " + imagePath);

        return loadImageSmall(Objects.requireNonNullElse(imagePath, "src/main/resources/com/example/song_finder_fx/images/manual_claims/upload_artwork_90.jpg"), true);
        // String location = "src/main/resources/com/example/song_finder_fx/images/manual_claims/upload_artwork_90.jpg";
    }

    public static String findUPCImage(String searchLocation, String upc) {
        // Create a File object for the search location
        File searchDir = new File(searchLocation);

        // Find the UPC folder
        File upcFolder = findUPCFolder(searchDir, upc);
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
        /*File[] folders = searchDir.listFiles((dir, name) -> name.equalsIgnoreCase(upc) && new File(dir, name).isDirectory());

        return (folders != null && folders.length > 0) ? folders[0] : null;*/

        File[] folders = searchDir.listFiles((dir, name) -> name.toLowerCase().startsWith(upc.toLowerCase()) && new File(dir, name).isDirectory());

        return (folders != null && folders.length > 0) ? folders[0] : null;
    }

    private static File findUPCImageFile(File upcFolder, String upc) {
        File[] imageFiles = upcFolder.listFiles((dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return (lowercaseName.startsWith(upc.toLowerCase()) &&
                    (lowercaseName.endsWith(".png") || lowercaseName.endsWith(".jpg") || lowercaseName.endsWith(".jpeg")));
        });

        return (imageFiles != null && imageFiles.length > 0) ? imageFiles[0] : null;
    }

    private static void addSongSummaryRow(Table table, Image songImage, String songName, String splits, String tracks, String artistShareAUD, String artistShareLKR) {
        VerticalAlignment verticalAlignment = VerticalAlignment.MIDDLE;
        Color backgroundColor = new DeviceRgb(226, 229, 233);
        PdfFont subtitleFont = FONT_POPPINS;
        TextAlignment textAlignment = TextAlignment.CENTER;
        Border border = DARK_BLUE_BORDER;

        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setHeight(1f).add(new Paragraph("")).setBorder(Border.NO_BORDER));

        // Songs
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(songImage).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(songName).setFont(FONT_POPPINS_MEDIUM)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(splits).setFont(subtitleFont).setTextAlignment(textAlignment)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(tracks).setFont(subtitleFont).setTextAlignment(textAlignment)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(artistShareAUD).setFont(subtitleFont).setTextAlignment(textAlignment)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));
        table.addCell(new Cell().setHeight(30f).setBackgroundColor(backgroundColor).add(new Paragraph(artistShareLKR).setFont(subtitleFont).setTextAlignment(textAlignment)).setVerticalAlignment(verticalAlignment).setFontSize(10f).setBorder(border));
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

    static Image loadImageSmall(String location, boolean autoscale) throws IOException {
        /*BufferedImage bufferedImage = ImageIO.read(new File(location));
        BufferedImage resizedImage = ImageProcessor.resizeImage(100, 100, bufferedImage);
        String format = location.toLowerCase().endsWith(".png") ? "png" : "jpeg";
        byte[] imageBytes = convertToByteArray(resizedImage, format);
        Image image = new Image(ImageDataFactory.create(imageBytes));
        image.setAutoScale(autoscale);
        return image;*/

        Image image = new Image(ImageDataFactory.create(location));

        // Set the new dimensions
        image.setWidth(30f);
        image.setHeight(30f);

        image.setAutoScale(autoscale);

        return image;
    }

    public static byte[] convertToByteArray(BufferedImage image, String format) throws IOException {
        if (image == null) {
            return null;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            return baos.toByteArray();
        }
    }

    public static BufferedImage loadImage(String imagePath) {
        if (imagePath == null) {
            return null;
        }

        try {
            File imageFile = new File(imagePath);
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            return null;
        }
    }

    private static String getArtistHeadingImage(String artistName) {
        if (Objects.equals(artistName, "Ridma Weerawardena")) {
            return "src/main/resources/com/example/song_finder_fx/images/reports/artists/ridmaw.png";
        } else {
            return "src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png";
        }
    }
}
