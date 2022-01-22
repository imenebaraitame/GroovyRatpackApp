package com.corposense.ocr.demo;


import com.google.inject.Inject;
import com.itextpdf.text.DocumentException;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.im4java.core.IM4JavaException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExtractImage extends PDFStreamEngine {

    @Inject
    public ExtractImage() throws IOException {
    }

    private int imageNumber = 1;


    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        String operation = operator.getName();
        if ("Do".equals(operation)) {
            COSName objectName = (COSName) operands.get(0);
            PDXObject xobject = getResources().getXObject(objectName);
            if (xobject instanceof PDImageXObject) {
                PDImageXObject image = (PDImageXObject) xobject;

                // save image to local
                BufferedImage bImage = image.getImage();
                String pathName = "ExtractedImage_" + imageNumber + ".png";
                File file = new File(pathName);
                ImageIO.write(bImage, "PNG", file);
                System.out.println("Image saved.");
/*
                try {
                    String imageNBorder = ImageProcess.ImgAfterDeskewingWithoutBorder(pathName, imageNumber);
                    String finalImage = ImageProcess.ImgAfterRemovingBackground(pathName, imageNumber);

                    // configfileValue = 0->make the image visible, =1->make the image invisible
                    SearchableImagePdf createPdf = new SearchableImagePdf
                            (finalImage, "./textonly_pdf_", "0");
                    createPdf.textOnlyPdf(finalImage, imageNumber);

                    ImageLocationsAndSize.createPdfWithOriginalImage("./textonly_pdf_" + imageNumber + ".pdf",
                            "./newFile_pdf_" + imageNumber + ".pdf", imageNBorder);

 */
/*
                    //Extract text from the image.
                    ImageText ocr = new ImageText(finalImage);
                    String fulltext = ocr.generateText();

                    System.out.println("Creating pdf document...");
                    TextPdf textpdf = new TextPdf(fulltext, "./ocrDemo_pdf_" + imageNumber + ".pdf");
                    System.out.println("Document "+ imageNumber +" created.");
                    textpdf.generateDocument(fulltext,imageNumber);

 */

                    imageNumber++;
/*
                } catch (DocumentException | IM4JavaException | InterruptedException e) {
                    e.printStackTrace();
                }

 */
            }else if (xobject instanceof PDFormXObject) {
                PDFormXObject form = (PDFormXObject) xobject;
                showForm(form);
            }
        } else {
            super.processOperator(operator, operands);
        }

    }


   public static void takeImageFromPdf (String fileName) throws IOException {
	   PDDocument document = null;
       try
       {
           document = PDDocument.load( new File(fileName) );
           ExtractImage printer = new ExtractImage();
           int pageNum = 0;
           for( PDPage page : document.getPages() )
           {
               pageNum++;
               System.out.println( "Processing page: " + pageNum );
               printer.processPage(page);
           }
       }
       finally
       {
           if( document != null )
           {
               document.close();
           }
       }
   }

   public static int pdfPageNumber(String fileName) throws IOException {

       PDDocument doc = PDDocument.load(new File(fileName));
       int pageNum = doc.getNumberOfPages();
       System.out.println(pageNum);
       doc.close();
       return pageNum;
   }


   public static void mergePdfDocuments(String fileName, String inputFile, String outputFile) throws IOException {

        //Loading an existing PDF document
        //Create PDFMergerUtility class object
        PDFMergerUtility PDFmerger = new PDFMergerUtility();
        PDDocument document = null;

        //use the input fileName to get the number of pages
        document = PDDocument.load(new File(fileName));

        //Setting the destination file path
        PDFmerger.setDestinationFileName(outputFile);
        int pageNum = 0;
        for (PDPage Page : document.getPages()) {

            pageNum++;
            File file1 = new File(inputFile + pageNum + ".pdf");
            PDDocument document1 = PDDocument.load(file1);

            //adding the source files
            PDFmerger.addSource(file1);

            //Merging the documents
            PDFmerger.mergeDocuments(null);

            System.out.println("PDF Documents merged to a single file successfully");

            //Close documents
            document.close();
            document1.close();

        }
    }



      
	 
}