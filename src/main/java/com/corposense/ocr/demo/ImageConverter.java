package com.corposense.ocr.demo;

import com.google.inject.Inject;
import org.im4java.core.IM4JavaException;

import java.io.IOException;

public class ImageConverter {

    @Inject
    ImageConverter(){

    }
    public String createTextOnlyPdf(String inputFile) throws IOException, InterruptedException, IM4JavaException {
        String imageNBorder = ImageProcessing.ImgAfterDeskewingWithoutBorder(inputFile, 1);
        String finalImage = ImageProcessing.ImgAfterRemovingBackground(inputFile, 1);

        // configfileValue = 0->make the image visible, =1->make the image invisible
        SearchableImagePdf createPdf = new SearchableImagePdf(finalImage,
                "./textonly_pdf_", "0");
        createPdf.textOnlyPdf(finalImage, 1);

        System.out.println("getting the size and the location of the image from textonly_pdf_1");
        return imageNBorder;
    }

    public String produceText(String inputFile) throws IOException, InterruptedException, IM4JavaException {
        ImageProcessing.ImgAfterDeskewingWithoutBorder(inputFile, 1);
        String finalImage = ImageProcessing.ImgAfterRemovingBackground(inputFile, 1);
        //Extract text from the image.
        ImageText ocr = new ImageText(finalImage);
        String fulltext = ocr.generateText();

        System.out.println("Creating pdf document...");

        return  fulltext;
    }

}