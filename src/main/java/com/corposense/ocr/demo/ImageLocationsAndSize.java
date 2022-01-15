package com.corposense.ocr.demo;

import com.google.inject.Inject;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.state.*;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class ImageLocationsAndSize extends PDFStreamEngine{
	
	
	 private float imageYPosition, imageXPosition, imageXScale, imageYScale;
	 
        /**
	     * @throws IOException If there is an error loading text stripper properties.
	     */ 
	 @Inject
    public ImageLocationsAndSize() throws IOException
    {
        // preparing PDFStreamEngine
        addOperator(new Concatenate());
        addOperator(new DrawObject());
        addOperator(new SetGraphicsStateParameters());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new SetMatrix());
    }
    
    
    //@Override
	public void processOperator( Operator operator, List<COSBase> operands) throws IOException
    {  
		String operation = operator.getName();
        if( "Do".equals(operation) )
        {  
           COSName objectName = (COSName) operands.get( 0 );
           // get the PDF object
	       PDXObject xobject = getResources().getXObject( objectName );
	       // check if the object is an image object
            if(xobject  instanceof PDImageXObject) {
            	findImageScaleAndPosition();
            }
            else if(xobject instanceof PDFormXObject)
            {
                PDFormXObject form = (PDFormXObject)xobject ;
                showForm(form);
            }
        }
            else
            {
                super.processOperator( operator, operands);
            } 
        }
    
	
	private void findImageScaleAndPosition() {
		 Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
         // displayed size in user space units
             imageXScale = ctmNew.getScalingFactorX();
             imageYScale = ctmNew.getScalingFactorY();
          // position of image in the pdf in terms of user space units
             imageXPosition = ctmNew.getTranslateX();
             imageYPosition = ctmNew.getTranslateY();
		
	}

	/**
     * Place image on existing pdf.
     * @param inputFilePath
     * @param outputFilePath
     * @param imgPath
     * @throws DocumentException
     * @throws IOException
     */ 
	public void PalceImageOnExistingPdf(String inputFilePath,String outputFilePath,String imgPath ) throws DocumentException, IOException {
	    OutputStream file = new FileOutputStream(new File(outputFilePath));

	    PdfReader pdfReader = new PdfReader(inputFilePath);
	    PdfStamper pdfStamper = new PdfStamper(pdfReader, file);
	    //Image to be added in existing pdf file.
	    Image Img = Image.getInstance(imgPath);
	    //Scale image's width and height
	    Img.scaleAbsolute(imageXScale, imageYScale);
	    //Set position for image in PDF
	    Img.setAbsolutePosition(imageXPosition,imageYPosition);
	    
	    
	     // loop on all the PDF pages
	     // i is the pdfPageNumber
	     for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
	    //getOverContent() allows you to write pdfContentByte on TOP of existing pdf pdfContentByte.
	      
	           PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
	           pdfContentByte.addImage(Img);
	           
	     }
	     System.out.println("Document will be created at: "+ new File(outputFilePath).getPath() );
	     pdfStamper.close();
	   
		}

	public static void createPdfWithOriginalImage(String ExistingPdfFilePath, String outputFilePath, String imageNBorder) 
			           throws IOException, DocumentException {
		   PDDocument document = null;
	        try {

	       	document = PDDocument.load(new File(ExistingPdfFilePath));
	           ImageLocationsAndSize printer = new ImageLocationsAndSize();

	           for( PDPage page : document.getPages() )
	           {
	               printer.processPage(page);
	           }
	           document.close();
	           System.out.println("Document created.");

	        //Place the original image on top of transparent image in existing PDF.
	           printer.PalceImageOnExistingPdf(ExistingPdfFilePath, outputFilePath, imageNBorder);
	        }
	        finally {
	       	 if(document != null)
	       	 {
	       		 document.close();
	       	 }
	        }
	}

}

