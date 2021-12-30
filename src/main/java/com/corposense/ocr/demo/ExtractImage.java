package com.corposense.ocr.demo;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExtractImage extends PDFStreamEngine { 
	   
   public ExtractImage() throws IOException {
	   	
	    }
   
   
   public int imageNumber = 1;
   
   
   @Override 
   protected void processOperator( Operator operator, List<COSBase> operands) throws IOException
   {
       String operation = operator.getName();
       if( "Do".equals(operation) )
       {
           COSName objectName = (COSName) operands.get( 0 );
           PDXObject xobject = getResources().getXObject( objectName );
           if( xobject instanceof PDImageXObject)
           {
               PDImageXObject image = (PDImageXObject)xobject;


               BufferedImage bImage = image.getImage();
               ImageIO.write(bImage,"PNG",new File("ExtractedImage_"+imageNumber+".png"));
               System.out.println("Image saved.");
               imageNumber++;

           }
           else if(xobject instanceof PDFormXObject)
           {
               PDFormXObject form = (PDFormXObject)xobject;
               showForm(form);
           }
       }
       else
       {
           super.processOperator( operator, operands);
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
      
	 
}