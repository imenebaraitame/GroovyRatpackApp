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
    public String dirName = "public/generatedFiles/createdFiles";
    public File dir = new File (dirName);

@Inject
    public TextPdf(String fullText, String docPath) {
        this.fullText = fullText;
        this.docPath = docPath;
    }

    public String generateDocument(String fullText , int number) throws FileNotFoundException, DocumentException {
        Document document = new Document(PageSize.LETTER);
        //2) Get a PdfWriter instance
        String doc = new File(dir, docPath).toString();
        FileOutputStream fos = new FileOutputStream(doc);
        System.out.println("File will be created at: " + new File(dir,this.docPath).getPath());
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
        return doc;
    }

    public static void createTextOverlay(int pageNum) throws DocumentException,
            IOException, InterruptedException, IM4JavaException {
        for( int i = 1 ; i <= pageNum; i++){
            String extractedImgName = "ExtractedImage_" + i + ".png";
            ImageProcessing image = new ImageProcessing(extractedImgName);
            String imageDeskew = image.deskewImage(extractedImgName, i);
            String imageNBorder = image.removeBorder(imageDeskew,i);
            String binaryInv = image.binaryInverse(imageNBorder, i);
            String finalImage = image.imageTransparent(imageNBorder,binaryInv, i);

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
