package com.corposense.ocr.demo;


import com.google.inject.Inject;
import com.itextpdf.text.DocumentException;
import net.sourceforge.tess4j.ITesseract.RenderedFormat;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.im4java.core.IM4JavaException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchableImagePdf {
	String input_file; String output_file; String configfileValue;
	public String dirName = "createdFiles";
	public File dir = new File (dirName);

	

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
		String img = new File(dir,imagePath).toString();
		String outputfile = new File(dir,output_file).toString();
	    instance.createDocuments(new String[]{img}, new String[]{outputfile +number}, formats);
	    
		} catch (TesseractException te){
			System.err.println("Error TE: " + te.getMessage());
		}

	}

	public static void createSearchablePdf( int pageNum) throws IOException, InterruptedException,
			                                                 IM4JavaException, DocumentException {

		for (int i = 1; i <= pageNum; i++) {
			String extractedImgName = "ExtractedImage_" + i + ".png";
			ImageProcessing image = new ImageProcessing(extractedImgName);
			String imageDeskew = image.deskewImage(extractedImgName, i);
			String imageNBorder = image.removeBorder(imageDeskew,i);
			String binaryInv = image.binaryInverse(imageNBorder, i);
			String finalImage = image.imageTransparent(imageNBorder,binaryInv, i);

			// configfileValue = 0->make the image visible, =1->make the image invisible
			SearchableImagePdf createPdf = new SearchableImagePdf
					(finalImage, "./textonly_pdf_", "0");
			createPdf.textOnlyPdf(finalImage, i);

			ImageLocationsAndSize.createPdfWithOriginalImage("textonly_pdf_" + i + ".pdf",
					"newFile_pdf_" + i + ".pdf", imageNBorder);
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
