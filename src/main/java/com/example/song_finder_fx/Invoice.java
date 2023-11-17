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
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
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
        Border grayBorder = new SolidBorder(Invoice.INVOICE_GRAY, 0.5f);


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

        // Getting Song List to generate Invoice

        int songListHeight = table03.getNumberOfRows();
        ResultSet songList = Database.getSongList();
        while (songList.next()) {
            String song = songList.getString("SONG");
            // System.out.println("song = " + song);
            String control = songList.getString("CONTROL");
            // System.out.println("control = " + control);
            String copyright = songList.getString("COPYRIGHT_OWNER");
            // System.out.println("copyright = " + copyright);

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
        }

        document.add(table01); // Header
        document.add(table02); // Invoice Details
        document.add(table03); // Song List
        document.close();
    }

    public static void main(String[] args) {
        /*generateInvoice("invoiceTo", "invoiceNo", date, "amountPerItem")''*/
    }
}
