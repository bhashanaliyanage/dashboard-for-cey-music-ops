package com.example.song_finder_fx;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Invoice {
    private static final Color INVOICEBLUE = new DeviceRgb(136, 193, 232);
    private static final Color INVOICLIGHTEBLUE = new DeviceRgb(232, 243, 251);
    private static final Color INVOICEWHITE = new DeviceRgb(255, 255, 255);

    public static void generateInvoice(String invoiceTo, String invoiceNo, LocalDate date, double amountPerItem) throws FileNotFoundException, MalformedURLException, SQLException, ClassNotFoundException {
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

        // Table 01
        float[] columnWidth = {1000f};
        Table table = new Table(columnWidth);

        //<editor-fold desc="Header">
        // Table 01 Row 01
        table.addCell(new Cell().add(ceyMusicLogo).setBorder(Border.NO_BORDER).setMargin(0f));
        // Table 01 Row 02
        table.addCell(new Cell()
                .add(new Paragraph(""))
                .setBackgroundColor(Invoice.INVOICLIGHTEBLUE)
                .setBorder(Border.NO_BORDER)
        );
        //</editor-fold>

        // Table 02
        float[] columnWidthTable02 = {100f, 300f, 200f};
        Table table02 = new Table(columnWidthTable02);
        table02.setMarginTop(20f);
        table02.setMarginLeft(30f);
        table02.setMarginRight(30f);
        // table02.setMarginBottom(20f);

        //<editor-fold desc="Sub Header">
        // Table 02 Row 01
        table02.addCell(new Cell().add(new Paragraph("INVOICE TO:"))
                .setBold()
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
        table02.addCell(new Cell().add(new Paragraph("INVOICE NO:"))
                .setBold()
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        table02.addCell(new Cell().add(new Paragraph(invoiceNo))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        table02.addCell(new Cell(2, 1).add(new Paragraph("{TOTAL DUE}"))
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(28f)
                .setBold()
                .setFontColor(Invoice.INVOICEBLUE)
                .setBorder(Border.NO_BORDER));

        // Table 02 Row 03
        table02.addCell(new Cell().add(new Paragraph("INVOICE DATE:"))
                .setBold()
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        table02.addCell(new Cell().add(new Paragraph(date.toString()))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        // table02.addCell(new Cell().add(new Paragraph("")));
        //</editor-fold>

        // Songs Table Header
        float[] columnWidthTable03and04 = {300f, 50f, 200f, 50f};
        Table table03 = new Table(columnWidthTable03and04);
        table03.setMarginTop(20f);
        table03.setMarginLeft(30f);
        table03.setMarginRight(30f);

        table03.addCell(new Cell().add(new Paragraph("SONG")))
                .setBackgroundColor(Invoice.INVOICEBLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER);
        table03.addCell(new Cell().add(new Paragraph("CONTROL")))
                .setBackgroundColor(Invoice.INVOICEBLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER);
        table03.addCell(new Cell().add(new Paragraph("COPYRIGHT OWNER")))
                .setBackgroundColor(Invoice.INVOICEBLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER);
        table03.addCell(new Cell().add(new Paragraph("AMOUNT")))
                .setBackgroundColor(Invoice.INVOICEBLUE)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER);

        // Songs Table Header
        Table table04 = new Table(columnWidthTable03and04);
        table04.setMarginLeft(30f);
        table04.setMarginRight(30f);

        // Getting Song List to generate Invoice
        ResultSet songList = Database.getSongList();
        while (songList.next()) {
            String song = songList.getString("SONG");
            // System.out.println("song = " + song);
            String control = songList.getString("CONTROL");
            // System.out.println("control = " + control);
            String copyright = songList.getString("COPYRIGHT_OWNER");
            // System.out.println("copyright = " + copyright);
            table04.addCell(new Cell().add(new Paragraph(song)))
                    .setBackgroundColor(Invoice.INVOICEWHITE);
            table04.addCell(new Cell().add(new Paragraph(control)))
                    .setBackgroundColor(Invoice.INVOICEWHITE);
            table04.addCell(new Cell().add(new Paragraph(copyright)))
                    .setBackgroundColor(Invoice.INVOICEWHITE);
            table04.addCell(new Cell().add(new Paragraph("{AMOUNT}")))
                    .setBackgroundColor(Invoice.INVOICEWHITE);
        }

        document.add(table);
        document.add(table02);
        document.add(table03);
        document.add(table04);
        document.close();
    }

    public static void main(String[] args) {
        // generateInvoice(invoiceTo, invoiceNo, date);
    }
}
