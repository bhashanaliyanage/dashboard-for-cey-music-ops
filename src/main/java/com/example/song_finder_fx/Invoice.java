package com.example.song_finder_fx;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
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
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

public class Invoice {
    private static final Color INVOICE_BLUE = new DeviceRgb(136, 193, 232);
    private static final Color INVOICE_LIGHT_BLUE = new DeviceRgb(232, 243, 251);
    private static final Color INVOICE_WHITE = new DeviceRgb(255, 255, 255);
    private static final Color INVOICE_GRAY = new DeviceRgb(204, 204, 204);

    public static void generateInvoice(String invoiceTo, String invoiceNo, LocalDate date, double amountPerItem, String currencyFormat) throws IOException, SQLException, ClassNotFoundException {
        String path = "invoice2.pdf";
        PdfWriter pdfWriter = new PdfWriter(path);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfDocument);
        document.setMargins(0f, 0f, 0f, 0f);

        String ceyMusicLogoPath = "src/main/resources/com/example/song_finder_fx/images/marketing-head-invoice-heading-cropped.png";
        ImageData data = ImageDataFactory.create(ceyMusicLogoPath);
        Image ceyMusicLogo = new Image(data);
        ceyMusicLogo.setAutoScale(true);

        // Loading RUBIK Font
        String RUBIK = "src/main/resources/com/example/song_finder_fx/fonts/Rubik-Regular.ttf";
        String RUBIK_SEMI_BOLD = "src/main/resources/com/example/song_finder_fx/fonts/Rubik-SemiBold.ttf";

        FontProgram fpRubik = FontProgramFactory.createFont(RUBIK);
        FontProgram fpRubikSemiBold = FontProgramFactory.createFont(RUBIK_SEMI_BOLD);

        PdfFont font_rubik = PdfFontFactory.createFont(fpRubik, PdfEncodings.WINANSI, true);
        PdfFont font_rubik_semi_bold = PdfFontFactory.createFont(fpRubikSemiBold, PdfEncodings.WINANSI, true);

        // Loading POPPINS Font
        String POPPINS = "src/main/resources/com/example/song_finder_fx/fonts/Poppins-Regular.ttf";
        FontProgram fpPoppins = FontProgramFactory.createFont(POPPINS);
        PdfFont font_poppins = PdfFontFactory.createFont(fpPoppins, PdfEncodings.WINANSI, true);

        // Table 01
        float[] columnWidth = {1000f};
        Table table01 = new Table(columnWidth);

        //<editor-fold desc="Header">
        // Table 01 Row 01
        table01.addCell(new Cell().add(ceyMusicLogo).setBorder(Border.NO_BORDER).setMargin(0f));
        // Table 01 Row 02
        table01.addCell(new Cell()
                .add(new Paragraph(""))
                .setBackgroundColor(Invoice.INVOICE_LIGHT_BLUE)
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

        String totalDue = Database.calculateTotalDue(amountPerItem);

        table02.addCell(new Cell(2, 1).add(new Paragraph(currencyFormat + " " + totalDue))
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(28f)
                .setBold()
                .setFontColor(Invoice.INVOICE_BLUE)
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

        // Songs Table Header
        float[] columnWidthTable03and04 = {260f, 70f, 200f, 70f};
        Table table03 = new Table(columnWidthTable03and04);
        table03.setMarginTop(10f);
        table03.setMarginLeft(30f);
        table03.setMarginRight(30f);
        table03.setFixedLayout();
        Border grayBorder = new SolidBorder(Invoice.INVOICE_GRAY, 0.5f);

        //<editor-fold desc="Table 03 Header">
        table03.addCell(new Cell().add(new Paragraph("SONG").setFont(font_rubik))
                .setFontColor(Invoice.INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(Invoice.INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        table03.addCell(new Cell().add(new Paragraph("CONTROL").setFont(font_rubik))
                .setFontColor(Invoice.INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(Invoice.INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        table03.addCell(new Cell().add(new Paragraph("COPYRIGHT OWNER").setFont(font_rubik))
                .setFontColor(Invoice.INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(Invoice.INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        table03.addCell(new Cell().add(new Paragraph("AMOUNT").setFont(font_rubik))
                .setFontColor(Invoice.INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(Invoice.INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        //</editor-fold>

        float[] columnWidthTableFooter = {200f, 200f, 200f};
        Table tableFooter = new Table(columnWidthTableFooter);
        tableFooter.setFixedPosition(30f, 20f, 535f);
        tableFooter.setBackgroundColor(Invoice.INVOICE_BLUE);
        tableFooter.setBorder(Border.NO_BORDER);
        tableFooter.addCell(new Cell().add(new Paragraph("Cell 01"))).setBorder(Border.NO_BORDER);
        tableFooter.addCell(new Cell().add(new Paragraph("Cell 02"))).setBorder(Border.NO_BORDER);
        tableFooter.addCell(new Cell().add(new Paragraph("Cell 03"))).setBorder(Border.NO_BORDER);

        document.add(table01); // Header
        document.add(table02); // Invoice Details
        document.add(tableFooter);

        // Getting Song List to generate Invoice
        int rowCount = 0;
        ResultSet songList = Database.getSongList();

        Table currentTable = createNewTable(); // createNewTable is a method to create a new table like table03
        currentTable.addCell(new Cell().add(new Paragraph("SONG").setFont(font_rubik))
                .setFontColor(Invoice.INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(Invoice.INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        currentTable.addCell(new Cell().add(new Paragraph("CONTROL").setFont(font_rubik))
                .setFontColor(Invoice.INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(Invoice.INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        currentTable.addCell(new Cell().add(new Paragraph("COPYRIGHT OWNER").setFont(font_rubik))
                .setFontColor(Invoice.INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(Invoice.INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        currentTable.addCell(new Cell().add(new Paragraph("AMOUNT").setFont(font_rubik))
                .setFontColor(Invoice.INVOICE_WHITE)
                .setHeight(40)
                .setBold()
                .setBorder(grayBorder)
                .setBackgroundColor(Invoice.INVOICE_BLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

        while (songList.next()) {
            //<editor-fold desc="Old Code">
            /*String song = songList.getString("SONG");
            String control = songList.getString("CONTROL");
            String copyright = songList.getString("COPYRIGHT_OWNER");

            table03.addCell(new Cell().add(new Paragraph(song)
                            .setFont(font_poppins)
                            .setMarginLeft(10f))
                    .setFontSize(11f)
                    .setBorder(grayBorder)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
            table03.addCell(new Cell().add(new Paragraph(control)
                            .setFont(font_poppins))
                    .setFontSize(11f)
                    .setBorder(grayBorder)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            if (copyright.contains(" | ")) {
                String[] splitString = copyright.split(" \\| ");
                table03.addCell(new Cell().add(new Paragraph(splitString[0] + "\n" + splitString[1])
                                .setFont(font_poppins))
                        .setFontSize(11f)
                        .setBorder(grayBorder)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE));
            } else {
                table03.addCell(new Cell().add(new Paragraph(copyright)
                                .setFont(font_poppins))
                        .setFontSize(11f)
                        .setBorder(grayBorder)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE));
            }
            if (Objects.equals(control, "50%")) {
                table03.addCell(new Cell().add(new Paragraph(currencyFormat + " " + amountPerItem)
                                .setFont(font_poppins))
                        .setFontSize(11f)
                        .setBorder(grayBorder)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE));
            } else {
                table03.addCell(new Cell().add(new Paragraph(currencyFormat + " " + (amountPerItem * 2))
                                .setFont(font_poppins))
                        .setFontSize(11f)
                        .setBorder(grayBorder)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE));
            }
            rowCount++;

            if (rowCount % 8 == 0) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }*/
            //</editor-fold>

            addRowToTable(currentTable, songList, font_poppins, amountPerItem, currencyFormat, grayBorder); // addRowToTable is a method to add a new row
            rowCount++;

            if (rowCount % 8 == 0) {
                // Add the current table to the document
                document.add(currentTable);

                // Start a new table
                currentTable = createNewTable();

                // Add a page break
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }
        }

        if (currentTable.getNumberOfRows() > 0) {
            document.add(currentTable);
        }

        document.close();
    }

    private static void addRowToTable(Table currentTable, ResultSet songList, PdfFont fontPoppins, double amountPerItem, String currencyFormat, Border grayBorder) throws SQLException {
        String song = songList.getString("SONG");
        String control = songList.getString("CONTROL");
        String copyright = songList.getString("COPYRIGHT_OWNER");

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
            currentTable.addCell(new Cell().add(new Paragraph(currencyFormat + " " + amountPerItem)
                            .setFont(fontPoppins))
                    .setFontSize(11f)
                    .setHeight(50)
                    .setBorder(grayBorder)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
        } else {
            currentTable.addCell(new Cell().add(new Paragraph(currencyFormat + " " + (amountPerItem * 2))
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
        table.setMarginLeft(30f);
        table.setMarginRight(30f);
        table.setFixedLayout();

        return table;
    }
}
