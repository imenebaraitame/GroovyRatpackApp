package com.corposense.ocr.demo;
<<<<<<< HEAD
=======

import com.google.inject.Inject;

>>>>>>> c82a67c9b59a18e9ee34d575f5018c472c9b842c
import net.sourceforge.tess4j.ITesseract.RenderedFormat;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CreateSearchableImagePdf {
	String input_file; String output_file; String configfileValue;

<<<<<<< HEAD

=======
	@Inject
>>>>>>> c82a67c9b59a18e9ee34d575f5018c472c9b842c
	public CreateSearchableImagePdf(String input_file, String output_file, String configfileValue){
		this.input_file = input_file;
		this.output_file = output_file;
		this.configfileValue = configfileValue;
	}
	 
	public void textOnlyPdf(String imagePath){
	 List<RenderedFormat> formats = new ArrayList<RenderedFormat>(Arrays.asList(RenderedFormat.PDF));
          try {
		
		Tesseract instance = new Tesseract();
		//mode 6: Assume a single uniform block of text.
		instance.setPageSegMode(6);
		instance.setTessVariable("user_defined_dpi", "300");
		instance.setDatapath(System.getenv("TESSDATA_PREFIX"));
		instance.setLanguage("ara+eng");//set the English and Arabic languages
	    instance.setTessVariable("textonly_pdf",configfileValue);
	    instance.createDocuments(new String[]{imagePath}, new String[]{output_file}, formats);
	    
		} catch (TesseractException te){
			System.err.println("Error TE: " + te.getMessage());
		}
	
    
	}
		
	
	

}
