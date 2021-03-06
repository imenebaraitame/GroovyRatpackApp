package com.corposense.ocr.demo;

import com.google.inject.Inject;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.nio.file.Paths;

public class ImageText extends Tesseract {

    private String imagePath;
    public String dirPath = Paths.get("public/generatedFiles/createdFiles").toAbsolutePath().toString();
    public File dir = new File(dirPath);

    @Inject
    public ImageText(String imagePath) {

        this.imagePath = imagePath;
    }

    public String generateText() {
        
        this.setTessVariable("user_defined_dpi", "300");
        this.setDatapath(System.getenv("TESSDATA_PREFIX"));
        this.setLanguage("ara+eng");//set the English and Arabic language

        String fullText = null;
        try {
            fullText = this.doOCR(new File(dir,imagePath));
        } catch (TesseractException e) {
            System.err.println("TesseractException:" + e.getMessage());
        }
        return fullText;
    }

}
