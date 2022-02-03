package com.corposense.ocr.demo;


import com.google.inject.Inject;
import com.itextpdf.text.DocumentException;
import net.sourceforge.tess4j.ITesseract.RenderedFormat;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.im4java.core.IM4JavaException;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchableImagePdf {
	String input_file; String output_file; String configfileValue;
	

	@Inject
	public SearchableImagePdf(String input_file, String output_file, String configfileValue){
		this.input_file = input_file;
		this.output_file = output_file;
		this.configfileValue = configfileValue;
	}
	 
	public void textOnlyPdf(String imagePath , int number){
	 List<RenderedFormat> formats = new ArrayList<RenderedFormat>(Arrays.asList(RenderedFormat.PDF));
          try {
		
		Tesseract instance = new Tesseract();
		//mode 6: Assume a single uniform block of text.
		instance.setPageSegMode(6);
		instance.setTessVariable("user_defined_dpi", "300");
		instance.setDatapath(System.getenv("TESSDATA_PREFIX"));
		instance.setLanguage("ara+eng");//set the English and Arabic languages
	    instance.setTessVariable("textonly_pdf_",configfileValue);
	    instance.createDocuments(new String[]{imagePath}, new String[]{output_file + number}, formats);
	    
		} catch (TesseractException te){
			System.err.println("Error TE: " + te.getMessage());
		}

	}

	public static void createSearchablePdf( int pageNum) throws IOException, InterruptedException,
			                                                 IM4JavaException, DocumentException {

		for (int i = 1; i <= pageNum; i++) {
			String extractedImgName = "ExtractedImage_" + i + ".png";
			String imageNBorder = ImageProcess.ImgAfterDeskewingWithoutBorder(extractedImgName, i);
			String finalImage = ImageProcess.ImgAfterRemovingBackground(extractedImgName, i);

			// configfileValue = 0->make the image visible, =1->make the image invisible
			SearchableImagePdf createPdf = new SearchableImagePdf
					(finalImage, "./textonly_pdf_", "0");
			createPdf.textOnlyPdf(finalImage, i);

			ImageLocationsAndSize.createPdfWithOriginalImage("./textonly_pdf_" + i + ".pdf",
					"./newFile_pdf_" + i + ".pdf", imageNBorder);
		}
	}
	/*
	public static void extractFonts(String inputFile) throws IOException {
		PDDocument doc = PDDocument.load(new File(inputFile));
		for (int i = 0; i < doc.getNumberOfPages(); ++i)
		{
			PDPage page = doc.getPage(i);
			PDResources res = page.getResources();
			for (COSName fontName : res.getFontNames())
			{
				PDFont font = res.getFont(fontName);
				boolean isEmbedded = font.isEmbedded();
				System.out.println("the file has fonts:" +isEmbedded);
			}
		}
	}

	 */

		
	
	

}
