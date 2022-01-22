package com.corposense.ocr.demo;

import com.google.inject.Inject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.im4java.core.IM4JavaException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextPdf {

    private final String fullText;
    private final String docPath;

@Inject
    public TextPdf(String fullText, String docPath) {
        this.fullText = fullText;
        this.docPath = docPath;
    }

    public void generateDocument(String fullText , int number) throws FileNotFoundException, DocumentException {
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

    public static void createTextOverlay(String inputFile, int pageNum) throws DocumentException,
            IOException, InterruptedException, IM4JavaException {
        for( int i = 1 ; i <= pageNum; i++){
            String extractedImgName = "ExtractedImage_" + i + ".png";
            String imageNBorder = ImageProcess.ImgAfterDeskewingWithoutBorder(extractedImgName, i);
            String finalImage = ImageProcess.ImgAfterRemovingBackground(extractedImgName, i);
            //Extract text from the image.
            ImageText ocr = new ImageText(finalImage);
            String fulltext = ocr.generateText();

            System.out.println("Creating pdf document...");
            TextPdf textpdf = new TextPdf(fulltext, "./ocrDemo_pdf_" + i + ".pdf");
            System.out.println("Document " + i + " created.");
            textpdf.generateDocument(fulltext, i);
        }

    }


}
