package com.corposense.ocr.demo;

import com.google.inject.Inject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class TextPdf {

    private final String fullText;
    private final String docPath;

@Inject
    public TextPdf(String fullText, String docPath) {
        this.fullText = fullText;
        this.docPath = docPath;
    }

    void generateDocument(String fullText , int number) throws FileNotFoundException, DocumentException {
        Document document = new Document(PageSize.LETTER);
        //2) Get a PdfWriter instance
        FileOutputStream fos = new FileOutputStream(this.docPath);
        System.out.println("File will be created at: " + new File(this.docPath).getPath());
        PdfWriter.getInstance(document, fos);
        //3) Open the Document
        document.open();
        //4) Add content
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Paragraph paragraph1 = new Paragraph("****** Result for Image/Page "+number+" ******");
        Paragraph paragraph2 = new Paragraph(fullText, font);
        document.add(paragraph1);
        document.add(paragraph2);
        //5) Close the document
        document.close();
    }


}
