package com.example.song_finder_fx;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public class Invoice {
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
        table.addCell(new Cell(1, 2).add(new Paragraph("Invoice").setTextAlignment(TextAlignment.RIGHT)));

        // Row 02
        table.addCell(new Cell().add(new Paragraph("")));
        table.addCell(new Cell().add(new Paragraph("Invoice To:").setTextAlignment(TextAlignment.RIGHT)));

        // Row 03
        table.addCell(new Cell().add(new Paragraph("")));
        table.addCell(new Cell().add(new Paragraph("{Invoice To:}").setTextAlignment(TextAlignment.RIGHT)));

        // Row 04
        table.addCell(new Cell().add(new Paragraph("Australian Account Details")));
        table.addCell(new Cell().add(new Paragraph("Sri Lankan Account Details")));
        table.addCell(new Cell().add(new Paragraph("Total Due").setTextAlignment(TextAlignment.RIGHT)));

        // Row 05-
        table.addCell(new Cell().add(new Paragraph("""
                CeyMusic Pty Ltd
                BSB: 062-914
                Account Number: 11079828
                BIC/SWIFT Code: CTBAAU2S
                """)));
        table.addCell(new Cell().add(new Paragraph("""
                Commercial Bank (Battaramulla Branch)
                CeyMusic Publishing Pvt Ltd
                1000462348
                Swift Code: CCEYLKLX
                """)));
        table.addCell(new Cell().add(new Paragraph("{Total Due}").setTextAlignment(TextAlignment.RIGHT)));

        document.add(table);
        document.close();
    }

    public static void main(String[] args) throws MalformedURLException, FileNotFoundException {
        generateTest();
    }
}
