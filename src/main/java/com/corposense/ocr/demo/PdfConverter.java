package com.corposense.ocr.demo;

import com.google.inject.Inject;
import com.itextpdf.text.DocumentException;
import org.im4java.core.IM4JavaException;


import java.io.IOException;

public class PdfConverter {
    @Inject
    public PdfConverter(){

    }
    public void produceSearchablePdf(String inputFile) throws IOException, DocumentException,
                                                                InterruptedException, IM4JavaException {
        int imageNum = ExtractImage.countImage(inputFile);
        ExtractImage.takeImageFromPdf(inputFile);
        SearchableImagePdf.createSearchablePdf(imageNum);
    }
    public void produceTextOverlay(String inputFile) throws IOException, DocumentException,
                                                            InterruptedException, IM4JavaException {
        int imageNum = ExtractImage.countImage(inputFile);
        ExtractImage.takeImageFromPdf(inputFile);
        TextPdf.createTextOverlay(imageNum);
    }
}
