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

public class Invoice {
    private static final Color INVOICEBLUE = new DeviceRgb(136, 193, 232);
    private static final Color INVOICLIGHTEBLUE = new DeviceRgb(232, 243, 251);

    public static void generateTest() throws FileNotFoundException, MalformedURLException {
        /*String path = Main.browseDestination().getAbsolutePath();*/
        String path = "invoice.pdf";
        PdfWriter pdfWriter = new PdfWriter(path);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfDocument);

        String ceyMusicLogoPath = "src/main/resources/com/example/song_finder_fx/images/cey_music_logo.png";
        ImageData data = ImageDataFactory.create(ceyMusicLogoPath);
        Image ceyMusicLogo = new Image(data);
        ceyMusicLogo.setWidth(100f);

        float[] columnWidth = {140, 140, 280};
        Table table = new Table(columnWidth);

        // Row 01
        table.addCell(new Cell(3, 1).add(ceyMusicLogo));
        table.addCell(new Cell().add(new Paragraph("")));
        table.addCell(new Cell(1, 2).add(new Paragraph("INVOICE")
                        .setFontSize(48f)
                        .setFontColor(Invoice.INVOICEBLUE)
                        .setBold()
                        .setTextAlignment(TextAlignment.RIGHT)));

        // Row 02
        table.addCell(new Cell().add(new Paragraph("")));
        table.addCell(new Cell().add(new Paragraph("INVOICE TO")
                        .setFontSize(12f)
                        .setTextAlignment(TextAlignment.RIGHT)));

        // Row 03
        table.addCell(new Cell().add(new Paragraph(""))
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("Sri Lankan Association Of Bangladesh")
                        .setFontSize(18f)
                        .setFontColor(Invoice.INVOICEBLUE)
                        .setBold()
                        .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER));

        // Row 04 Added
        table.addCell(new Cell().add(new Paragraph("INVOICE NO:"))
                .setFontSize(12f)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("CEY/SLAB/001")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        // Row 04 Added 02
        table.addCell(new Cell().add(new Paragraph("INVOICE DATE:"))
                .setFontSize(12f)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("10/02/2023")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        // Row 04
        table.addCell(new Cell().add(new Paragraph("Australian Account Details"))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("Sri Lankan Account Details"))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("TOTAL DUE")
                        .setFontSize(12f)
                        .setVerticalAlignment(VerticalAlignment.BOTTOM) // Not working
                        .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER));

        // Row 05-
        table.addCell(new Cell().add(new Paragraph("""
                        CeyMusic Pty Ltd
                        BSB: 062-914
                        Account Number: 11079828
                        BIC/SWIFT Code: CTBAAU2S
                        """))
                .setFontSize(9f)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("""
                        Commercial Bank (Battaramulla Branch)
                        CeyMusic Publishing Pvt Ltd
                        1000462348
                        Swift Code: CCEYLKLX
                        """))
                .setFontSize(9f)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("290 USD")
                        .setTextAlignment(TextAlignment.RIGHT))
                .setFontSize(30f)
                .setFontColor(Invoice.INVOICEBLUE)
                .setBold()
                .setVerticalAlignment(VerticalAlignment.TOP) // Not working
                .setBorder(Border.NO_BORDER));

        document.add(table);
        document.close();
    }

    public static void generateTest2() throws FileNotFoundException, MalformedURLException {
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

        // Table 01 Row 01
        table.addCell(new Cell().add(ceyMusicLogo).setBorder(Border.NO_BORDER).setMargin(0f));
        // Table 01 Row 02
        table.addCell(new Cell()
                .add(new Paragraph(""))
                .setBackgroundColor(Invoice.INVOICLIGHTEBLUE)
                .setBorder(Border.NO_BORDER)
        );

        // Table 02
        float[] columnWidthTable02 = {100f, 300f, 200f};
        Table table02 = new Table(columnWidthTable02);
        table02.setMarginTop(20f);
        table02.setMarginLeft(30f);
        table02.setMarginRight(30f);

        // Table 02 Row 01
        table02.addCell(new Cell().add(new Paragraph("INVOICE TO:"))
                .setBold()
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        table02.addCell(new Cell().add(new Paragraph("{INVOICE TO}"))
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
        table02.addCell(new Cell().add(new Paragraph("{INVOICE NO}"))
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
        table02.addCell(new Cell().add(new Paragraph("{DATE}"))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        // table02.addCell(new Cell().add(new Paragraph("")));

        document.add(table);
        document.add(table02);
        document.close();
    }

    public static void generatePDF() throws FileNotFoundException, MalformedURLException {
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

        //<editor-fold desc="Table 01: Header">
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

        //<editor-fold desc="Table 02: Invoice Main Details">
        // Table 02 Row 01
        table02.addCell(new Cell().add(new Paragraph("INVOICE TO:"))
                .setBold()
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        table02.addCell(new Cell().add(new Paragraph("{INVOICE TO}"))
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
        table02.addCell(new Cell().add(new Paragraph("{INVOICE NO}"))
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
        table02.addCell(new Cell().add(new Paragraph("{DATE}"))
                .setFontSize(11f)
                .setBorder(Border.NO_BORDER));
        // table02.addCell(new Cell().add(new Paragraph("")));
        //</editor-fold>

        document.add(table);
        document.add(table02);
        document.close();
    }

    public static void main(String[] args) throws MalformedURLException, FileNotFoundException {
        generateTest2();
    }
}
