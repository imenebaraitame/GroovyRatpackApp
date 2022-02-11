package com.corposense.ocr.demo;

import com.google.inject.Inject;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;


import java.io.File;
import java.io.IOException;

public class PdfCombiner {
    public static String dirName = "createdFiles";
    public static File dir = new File (dirName);

    @Inject
    public PdfCombiner(){

    }
    public static void mergePdfDocuments(String uploadedFile, String ocrFile , String outputFile) throws IOException {

        //Loading an existing PDF document
        //Create PDFMergerUtility class object
        PDFMergerUtility PDFmerger = new PDFMergerUtility();

        //Setting the destination file path
        PDFmerger.setDestinationFileName(outputFile);

        for (int i=1 ; i<= ExtractImage.countImage(uploadedFile); i++) {

            File file = new File(dir,ocrFile + i + ".pdf");
            PDDocument document = PDDocument.load(file);

            //adding the source files
            PDFmerger.addSource(file);

            //Merging the documents
            PDFmerger.mergeDocuments(null);

            System.out.println("PDF Documents merged to a single file successfully");

            //Close documents
            document.close();

        }
    }

}
