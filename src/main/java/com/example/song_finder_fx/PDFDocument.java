package com.example.song_finder_fx;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import java.io.FileNotFoundException;

public class PDFDocument {
    private static final PageSize PAGE_SIZE = PageSize.A4;

    public Document getDocument(String path) throws FileNotFoundException {
        PdfWriter pdfWriter = new PdfWriter(path);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PAGE_SIZE);
        Document document = new Document(pdfDocument);
        document.setMargins(0f, 0f, 0f, 0f);
        return document;
    }
}
