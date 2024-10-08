package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.Database;
import com.example.song_finder_fx.Model.Songs;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class Invoice implements com.example.song_finder_fx.Constants.Colors {
    /*private static final Color INVOICE_BLUE = new DeviceRgb(136, 193, 232);
    private static final Color INVOICE_LIGHT_BLUE = new DeviceRgb(232, 243, 251);
    private static final Color INVOICE_WHITE = new DeviceRgb(255, 255, 255);
    private static final Color INVOICE_GRAY = new DeviceRgb(204, 204, 204);*/
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static int pageNumber = 1;

    public static void generateInvoice(String invoiceTo, String invoiceNo, LocalDate date, double amountPerItem, String currencyFormat, double discount, Window window, ArrayList<Songs> songs) throws IOException, SQLException, ClassNotFoundException {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        chooser.setTitle("Save As");
        File pathTo = chooser.showSaveDialog(window);
        String path = pathTo.getAbsolutePath();

        Document document = getDocument(path);

        // Invoice Heading Image
        Image invoiceHeading = loadAutoScaledImage("src/main/resources/com/example/song_finder_fx/images/marketing-head-invoice-heading-cropped.png");

        PdfFont font_rubik = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Rubik-Regular.ttf");
        PdfFont font_rubik_semi_bold = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Rubik-SemiBold.ttf");
        PdfFont font_poppins = loadFont("src/main/resources/com/example/song_finder_fx/fonts/Poppins-Regular.ttf");

        // Table 01
        float[] columnWidth = {1000f};
        Table table01 = new Table(columnWidth);

        //<editor-fold desc="Header">
        // Table 01 Row 01
        table01.addCell(new Cell().add(invoiceHeading).setBorder(Border.NO_BORDER).setMargin(0f));
        // Table 01 Row 02
        table01.addCell(new Cell()
                .add(new Paragraph(""))
                .setBackgroundColor(INVOICE_LIGHT_BLUE)
                .setBorder(Border.NO_BORDER)
        );
        //</editor-fold>

        // Table 02
        float[] columnWidthTable02 = {100f, 300f, 200f};
        Table table02 = new Table(columnWidthTable02);
        table02.setMarginTop(20f);
        table02.setMarginLeft(30f);
        table02.setMarginRight(30f);
        table02.setFont(font_rubik);

        //<editor-fold desc="Sub Header">
        // Table 02 Row 01
        table02.addCell(new Cell().add(new Paragraph("INVOICE TO:").setFont(font_rubik_semi_bold))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        table02.addCell(new Cell().add(new Paragraph(invoiceTo))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        table02.addCell(new Cell().add(new Paragraph("TOTAL DUE"))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));

        // Table 02 Row 02
        table02.addCell(new Cell().add(new Paragraph("INVOICE NO:").setFont(font_rubik_semi_bold))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        table02.addCell(new Cell().add(new Paragraph(invoiceNo))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));

        int discountPercentage = (int) discount;
        double totalDue = Database.calculateTotalDue(amountPerItem);
        double discountAmount = ((totalDue * discountPercentage) / 100);
        totalDue = totalDue - discountAmount;
        double gst = (totalDue * 10) / 100;
        totalDue = totalDue + gst;

        table02.addCell(new Cell(2, 1).add(new Paragraph((currencyFormat + " " + df.format(totalDue))))
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(28f)
                .setBold()
                .setFontColor(INVOICE_BLUE)
                .setBorder(Border.NO_BORDER));

        // Table 02 Row 03
        table02.addCell(new Cell().add(new Paragraph("INVOICE DATE:").setFont(font_rubik_semi_bold))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        table02.addCell(new Cell().add(new Paragraph(date.toString()))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        // table02.addCell(new Cell().add(new Paragraph("")));
        //</editor-fold>

        // Songs Table
        float[] columnWidthTable03and04 = {260f, 70f, 200f, 70f};
        Table table03 = new Table(columnWidthTable03and04);
        table03.setMarginTop(10f);
        table03.setMarginLeft(30f);
        table03.setMarginRight(30f);
        table03.setFixedLayout();
        Border grayBorder = new SolidBorder(INVOICE_GRAY, 0.5f);

        //<editor-fold desc="Songs Table Header">
        table03.addCell(new Cell().add(new Paragraph("SONG").setFont(font_rubik))
                .setFontColor(INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        table03.addCell(new Cell().add(new Paragraph("CONTROL").setFont(font_rubik))
                .setFontColor(INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        table03.addCell(new Cell().add(new Paragraph("COPYRIGHT OWNER").setFont(font_rubik))
                .setFontColor(INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        table03.addCell(new Cell().add(new Paragraph("AMOUNT").setFont(font_rubik))
                .setFontColor(INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        //</editor-fold>

        // Table for Footer
        Table tableFooter = tableFooter(font_rubik);

        document.add(table01); // Header
        document.add(table02); // Invoice Details
        document.add(tableFooter);

        // Getting Song List to generate Invoice
        int rowCount = 0;

        Table currentTable = createNewTable(); // createNewTable is a method to create a new table like table03
        currentTable.addCell(new Cell().add(new Paragraph("SONG").setFont(font_rubik))
                .setFontColor(INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        currentTable.addCell(new Cell().add(new Paragraph("CONTROL").setFont(font_rubik))
                .setFontColor(INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        currentTable.addCell(new Cell().add(new Paragraph("COPYRIGHT OWNER").setFont(font_rubik))
                .setFontColor(INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        currentTable.addCell(new Cell().add(new Paragraph("AMOUNT").setFont(font_rubik))
                .setFontSize(12f)
                .setFontColor(INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(INVOICE_BLUE)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

        float[] columnWidthTableTotal = {470f, 130f};
        Table tableTotal = new Table(columnWidthTableTotal);
        tableTotal.setMarginLeft(30f);
        tableTotal.setMarginTop(30f);
        tableTotal.setMarginRight(30f);
        tableTotal.setFont(font_poppins);


        tableTotal.addCell(new Cell()
                .add(new Paragraph(discountPercentage + "% Discount"))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));
        tableTotal.addCell(new Cell()
                .add(new Paragraph("- " + currencyFormat + " " + df.format(discountAmount)))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));

        tableTotal.addCell(new Cell()
                .add(new Paragraph("10% GST"))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));
        tableTotal.addCell(new Cell()
                .add(new Paragraph("+ " + currencyFormat + " " + df.format(gst)))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));

        tableTotal.addCell(new Cell(1, 2)
                .setBackgroundColor(INVOICE_GRAY)
                .setBorder(Border.NO_BORDER)
                .setHeight(1f));

        tableTotal.addCell(new Cell()
                .add(new Paragraph("Total Due")
                        .setBold())
                .setFontSize(18f)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));
        tableTotal.addCell(new Cell()
                .add(new Paragraph(currencyFormat + " " + df.format(totalDue))
                        .setBold())
                .setFontSize(18f)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));

        while (rowCount < songs.size()) {
            addRowToTable(currentTable, songs.get(rowCount), font_poppins, amountPerItem, currencyFormat, grayBorder); // addRowToTable is a method to add a new row
            rowCount++;

            if (rowCount == 8) {
                // Add the current table to the document
                document.add(currentTable);

                // Start a new table
                currentTable = createNewTable();

                // Add a page break
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

                pageNumber++;

                document.add(tableFooter);
            } else if (rowCount > 8 && (rowCount - 8) % 14 == 0) {
                // Add the current table to the document
                document.add(currentTable);

                // Start a new table
                currentTable = createNewTable();

                // Add a page break
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

                pageNumber++;

                document.add(tableFooter);
            }

            /*if (pageNumber == 1) {
                if (rowCount % 8 == 0) {
                    // Add the current table to the document
                    document.add(currentTable);

                    // Start a new table
                    currentTable = createNewTable();

                    // Add a page break
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

                    pageNumber++;

                    document.add(tableFooter);
                }
            } else {
                if (currentTable.getNumberOfRows() == 14) {
                    // Add the current table to the document
                    document.add(currentTable);

                    // Start a new table
                    currentTable = createNewTable();

                    // Add a page break
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

                    pageNumber++;

                    document.add(tableFooter);
                }
            }*/
        }

        if (currentTable.getNumberOfRows() > 0) {
            document.add(currentTable);
        }

        int currentTableRowCount = currentTable.getNumberOfRows();

        if (pageNumber == 1) {
            if (currentTableRowCount <= 6) {
                document.add(tableTotal);
            } else if (currentTableRowCount == 7) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                pageNumber++;
                document.add(tableTotal);
                document.add(tableFooter);
            } else if (currentTableRowCount == 8) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                pageNumber++;
                document.add(tableTotal);
                document.add(tableFooter);
            }
        } else {
            if (currentTableRowCount <= 11) {
                document.add(tableTotal);
            } else {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                pageNumber++;
                document.add(tableTotal);
                document.add(tableFooter);
            }
        }

        document.close();
    }

    private static Document getDocument(String path) throws FileNotFoundException {
        PdfWriter pdfWriter = new PdfWriter(path);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfDocument);
        document.setMargins(0f, 0f, 0f, 0f);
        return document;
    }

    static PdfFont loadFont(String location) throws IOException {
        FontProgram fontProgram = FontProgramFactory.createFont(location);
        return PdfFontFactory.createFont(fontProgram, PdfEncodings.WINANSI, true);
    }

    static Image loadAutoScaledImage(String location) throws MalformedURLException {
        ImageData invoiceHeadingImageData = ImageDataFactory.create(location);
        Image invoiceHeading = new Image(invoiceHeadingImageData);
        invoiceHeading.setAutoScale(true);
        return invoiceHeading;
    }

    private static Image loadImage(String location) throws MalformedURLException {
        ImageData invoiceHeadingImageData = ImageDataFactory.create(location);
        return new Image(invoiceHeadingImageData);
    }

    private static Table tableFooter(PdfFont font) throws MalformedURLException {
        //<editor-fold desc="Images">
        // Invoice Footer Map Icon Image
        Image mapIcon = loadImage("src/main/resources/com/example/song_finder_fx/images/icon_alternate_map_marker.png");
        mapIcon.setWidth(10f);
        // mapIcon.setAutoScale(true);

        // Invoice Footer Globe Icon Image
        Image globeIcon = loadImage("src/main/resources/com/example/song_finder_fx/images/icon_globe.png");
        globeIcon.setWidth(10f);
        // globeIcon.setAutoScale(true);
        //</editor-fold>

        // Making Table
        float[] columnWidthTableFooter = {275f, 200f, 125f};
        Table tableFooter = new Table(columnWidthTableFooter);
        tableFooter.setFixedPosition(30f, 20f, 535f);
        tableFooter.setBackgroundColor(INVOICE_BLUE);

        float fontSize = 8f;

        // Making cell tables
        float[] columnWidthCell = {50f, 550f};
        Table tableForCell1 = new Table(columnWidthCell);
        tableForCell1.addCell(new Cell().add(mapIcon).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.RIGHT)
                .setBorder(Border.NO_BORDER));
        tableForCell1.addCell(new Cell().add(new Paragraph("65/18, Sandun Gardens, Elhena Rd, Maharagama")
                        .setFont(font)
                        .setFontColor(INVOICE_WHITE))
                .setFontSize(fontSize)
                .setBorder(Border.NO_BORDER));

        Table tableForCell2 = new Table(columnWidthCell);
        tableForCell2.addCell(new Cell().add(mapIcon).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.RIGHT)
                .setBorder(Border.NO_BORDER));
        tableForCell2.addCell(new Cell().add(new Paragraph("39 Annabelle View, Coombs ACT 2611")
                        .setFont(font)
                        .setFontColor(INVOICE_WHITE))
                .setFontSize(fontSize)
                .setBorder(Border.NO_BORDER));

        Table tableForCell3 = new Table(columnWidthCell);
        tableForCell3.addCell(new Cell().add(globeIcon).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.RIGHT)
                .setBorder(Border.NO_BORDER));
        tableForCell3.addCell(new Cell().add(new Paragraph("www.ceymusic.com.au")
                        .setFont(font)
                        .setFontColor(INVOICE_WHITE))
                .setFontSize(fontSize)
                .setBorder(Border.NO_BORDER));

        tableFooter.addCell(new Cell().add(tableForCell1).setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(10f)
                .setHeight(35f));
        tableFooter.addCell(new Cell().add(tableForCell2).setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(Border.NO_BORDER));
        tableFooter.addCell(new Cell().add(tableForCell3).setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(Border.NO_BORDER));
        return tableFooter;
    }

    private static void addRowToTable(Table currentTable, Songs songList, PdfFont fontPoppins, double amountPerItem, String currencyFormat, Border grayBorder) throws SQLException {
        String song = songList.getTrackTitle();
        String control = songList.getControl();
        String copyright = songList.getCopyrightOwner();

        currentTable.addCell(new Cell().add(new Paragraph(song)
                        .setFont(fontPoppins))
                .setFontSize(11f)
                .setHeight(50)
                .setBorder(grayBorder)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        currentTable.addCell(new Cell().add(new Paragraph(control)
                        .setFont(fontPoppins))
                .setFontSize(11f)
                .setHeight(50)
                .setBorder(grayBorder)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        if (copyright.contains(" | ")) {
            String[] splitString = copyright.split(" \\| ");
            currentTable.addCell(new Cell().add(new Paragraph(splitString[0] + "\n" + splitString[1])
                            .setFont(fontPoppins))
                    .setFontSize(11f)
                    .setHeight(50)
                    .setBorder(grayBorder)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
        } else {
            currentTable.addCell(new Cell().add(new Paragraph(copyright)
                            .setFont(fontPoppins))
                    .setFontSize(11f)
                    .setHeight(50)
                    .setBorder(grayBorder)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
        }
        if (Objects.equals(control, "50%")) {
            currentTable.addCell(new Cell().add(new Paragraph(currencyFormat + " " + df.format(amountPerItem))
                            .setFont(fontPoppins))
                    .setFontSize(11f)
                    .setHeight(50)
                    .setBorder(grayBorder)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
        } else {
            currentTable.addCell(new Cell().add(new Paragraph(currencyFormat + " " + df.format(amountPerItem * 2))
                            .setFont(fontPoppins))
                    .setFontSize(11f)
                    .setHeight(50)
                    .setBorder(grayBorder)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
        }
    }

    private static Table createNewTable() {
        float[] columnWidth = {260f, 70f, 200f, 70f};
        Table table = new Table(columnWidth);
        table.setMarginTop(10f);
        table.setMarginLeft(30f);
        table.setMarginRight(30f);
        table.setFixedLayout();

        return table;
    }
}
