package com.example.song_finder_fx.Model;

import com.example.song_finder_fx.Constants.Colors;
import com.example.song_finder_fx.Constants.SearchType;
import com.example.song_finder_fx.Controller.PDFDocument;
import com.example.song_finder_fx.Main;
import com.example.song_finder_fx.Organizer.SongSearch;
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
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.google.common.io.Files.getFileExtension;

public class ReportPDFNew implements Colors {
    private static PdfFont FONT_POPPINS_SEMIBOLD = null;
    private static PdfFont FONT_RUBIK_SEMIBOLD = null;
    private static PdfFont FONT_POPPINS = null;
    private static PdfFont FONT_POPPINS_MEDIUM = null;
    private static final Border DARK_BLUE_BORDER = new SolidBorder(INVOICE_DARK_BLUE, 0.5f);
    private String reportPath;
    private static final int PAGE01 = 1;
    private static final int PAGE02 = 2;

    public void generateReport(String path, ArtistReport report) throws IOException, SQLException, ClassNotFoundException {
        // Create document
        PDFDocument pdfDocument = new PDFDocument();
        Document document = pdfDocument.getDocument(path);

        setBackgroundColor(document);

        // Images
        Image reportHeading = getArtistHeading(report.getArtist().getName(), PAGE01);
        Image reportHeadingSmall = getArtistHeading(report.getArtist().getName(), PAGE02);

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
        reportSummary.setFixedPosition(35, 570, 540);
        Table songBreakdown = getSongBreakdownTable(report);
        songBreakdown.setWidth(540f);
        Table streamingBreakdown = getStreamingBreakdownTable(report);
        streamingBreakdown.setWidth(540f);

        document.add(tableHeader); // Letter Head
        document.add(reportMonthAndYear); // Report Month and Year
        document.add(reportSummary);
        document.add(songBreakdown);

        // Page 02 /////////////////////////////////////////////////////////////////////////////////////////////////////
        document.add(new AreaBreak());

        document.add(reportHeadingSmall);
        document.add(streamingBreakdown);

        document.close();

        this.reportPath = path;
    }

    private Table getStreamingBreakdownTable(ArtistReport report) throws MalformedURLException {
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
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("Amount").setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("Streams").setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));

        // Cell cell = new Cell().setBackgroundColor(backgroundColor).setHeight(30f).setBorder(border).add(new Image(ImageDataFactory.create("src/main/resources/com/example/song_finder_fx/images/spotify.png")).setAutoScale(true));

        /*Cell cell = new Cell()
                .setBackgroundColor(backgroundColor)
                .setHeight(30f)
                .setBorder(border)
                .add(new Image(ImageDataFactory.create("src/main/resources/com/example/song_finder_fx/images/spotify.png")).setAutoScale(true));

        cell.setNextRenderer(new RoundedBorderCellRenderer(cell));

        table.addCell(cell);*/

        // Values
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Image(ImageDataFactory.create("src/main/resources/com/example/song_finder_fx/images/spotify.png")).setAutoScale(true)));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("Spotify").setFont(subtitleFont).setTextAlignment(TextAlignment.LEFT).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("00.00").setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("0").setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));

        table.addCell(new Cell(1, 4).setBorder(border).add(new Paragraph("")));

        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Image(ImageDataFactory.create("src/main/resources/com/example/song_finder_fx/images/itunes.png")).setAutoScale(true)));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("Apple Music").setFont(subtitleFont).setTextAlignment(TextAlignment.LEFT).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("00.00").setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("0").setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));

        table.addCell(new Cell(1, 4).setBorder(border).add(new Paragraph("")));

        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Image(ImageDataFactory.create("src/main/resources/com/example/song_finder_fx/images/tiktok.png")).setAutoScale(true)));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("YouTube").setFont(subtitleFont).setTextAlignment(TextAlignment.LEFT).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("00.00").setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));
        table.addCell(new Cell().setHeight(30f).setBorder(border).add(new Paragraph("0").setFont(subtitleFont).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSizeSubTitle)).setVerticalAlignment(verticalAlignment));

        return table;
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

        System.out.println("Image Search Location: " + searchLocation);

        return loadImageSmall(Objects.requireNonNullElse(imagePath, "src/main/resources/com/example/song_finder_fx/images/manual_claims/upload_artwork_90.jpg"), true);
        // String location = "src/main/resources/com/example/song_finder_fx/images/manual_claims/upload_artwork_90.jpg";
    }

    public static String findUPCImage(String searchLocation, String upc) throws IOException {
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

    static Image getArtistHeading(String artistName, int pageNumber) throws MalformedURLException {
        Image invoiceHeading = new Image(ImageDataFactory.create(getArtistHeadingImage(artistName, pageNumber)));
        invoiceHeading.setAutoScale(true);
        return invoiceHeading;
    }

    static Image loadImage(String location, boolean autoscale) throws MalformedURLException {
        Image image = new Image(ImageDataFactory.create(location));
        image.setAutoScale(autoscale);
        return image;
    }

    static Image loadImageSmall(String location, boolean autoscale) throws IOException {
        Image image = new Image(ImageDataFactory.create(location));

        // Set the new dimensions
        image.setWidth(30f);
        image.setHeight(30f);

        image.setAutoScale(autoscale);

        return image;
    }

    private static String getArtistHeadingImage(String artistName, int pageNumber) {
        if (Objects.equals(artistName, "Ridma Weerawardena")) {
            if (pageNumber == 1)
                return "src/main/resources/com/example/song_finder_fx/images/reports/artists/ridmaw.png";
            else if (pageNumber == 2)
                return "src/main/resources/com/example/song_finder_fx/images/reports/artistsll./ridmaw_head_smapng";
            else return "src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png";
        } else return "src/main/resources/com/example/song_finder_fx/images/marketing-head-report-2.png";
    }

    public String getReportPath() {
        return this.reportPath;
    }

    private static class RoundedBorderCellRenderer extends CellRenderer {
        public RoundedBorderCellRenderer(Cell modelElement) {
            super(modelElement);
        }

        @Override
        public void draw(DrawContext drawContext) {
            drawContext.getCanvas().roundRectangle(getOccupiedAreaBBox().getX() + 1.5f, getOccupiedAreaBBox().getY() + 1.5f,
                    getOccupiedAreaBBox().getWidth() - 3, getOccupiedAreaBBox().getHeight() - 3, 10);
            drawContext.getCanvas().stroke();
            super.draw(drawContext);
        }
    }
}
