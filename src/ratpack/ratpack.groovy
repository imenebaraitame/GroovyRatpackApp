import app.model.FileService
import app.services.DefaultFileService
import com.corposense.ocr.demo.CreateSearchableImagePdf
import com.corposense.ocr.demo.extractImage
import com.corposense.ocr.demo.ImageLocationsAndSize
import com.corposense.ocr.demo.ImageProcess
import com.corposense.ocr.demo.ImageText
import com.corposense.ocr.demo.TextPdf
import com.corposense.ocr.demo.Utils

import ratpack.form.Form
import ratpack.form.UploadedFile
import ratpack.thymeleaf3.ThymeleafModule

import java.nio.file.Path

import static ratpack.thymeleaf3.Template.thymeleafTemplate
import static ratpack.groovy.Groovy.ratpack
import ratpack.server.BaseDir
import java.nio.file.Paths
import ratpack.file.MimeTypes

String uploadDir = 'uploads'
String publicDir = 'public'
String generatedFilesDir = "generatedFiles"

Path baseDir = BaseDir.find("${publicDir}/${uploadDir}")
Path baseGeneratedFilesDir = BaseDir.find("${publicDir}/${generatedFilesDir}")
//def baseDir = BaseDir.findBaseDir()
//def baseDir = BaseDir.find(".")

Path generatedFilesPath = baseGeneratedFilesDir.resolve(generatedFilesDir)
Path uploadPath = baseDir.resolve(uploadDir)
//def uploadPath = baseDir.getRoot().resolve(uploadDir)
//def uploadPath = baseDir.getRoot().resolve("${publicDir}/${uploadDir}")

ratpack {
    bindings {
        module (ThymeleafModule)
        bind (FileService, DefaultFileService)
        bind(ImageLocationsAndSize)
        bind(Utils)
        bind(CreateSearchableImagePdf)
        bind(ImageProcess)
        bind(extractImage)
        bind(ImageText)
        bind(TextPdf)
    }
    handlers {
        prefix("upload"){
            post {
                CreateSearchableImagePdf createSearchableImagePdf,
                ImageLocationsAndSize imageLocationsAndSize, ImageProcess imageProcess,
                extractImage extractImage,
                ImageText imagetext,
                TextPdf textPdf,
                FileService fileService->
                    parse(Form.class).then({ Form form ->
                        UploadedFile f = form.file("upload")
                        String name = fileService.save(f, uploadPath.toString())
                        String contentType = context.get(MimeTypes).getContentType(name)
                        File filePath = new File("${uploadPath}/${name}")
                        String inputFile = filePath.toString()

                            if (contentType.contains("application/pdf")) {
                                extractImage.takeImageFromPdf(inputFile);

                                String outputFilePath1 = "mergedImgPdf.pdf"
                                File outputFile1 = new File(generatedFilesPath.toString(), "${outputFilePath1}")
                                println(outputFile1)
                                extractImage.MergePdfDocuments(inputFile,"./newFile_pdf_", outputFile1.toString());

                                /*
                                String outputFilePath2 = "mergedText.pdf"
                                File outputFile2 = new File(generatedFilesPath.toString(), "${outputFilePath2}")
                                println(outputFile2)
                                extractImage.MergePdfDocuments(inputFile,"./ocrDemo_pdf_",outputFile2.toString());

                                 */


                                redirect "/show/$outputFilePath1"

                            } else {

                                String imageNBorder = imageProcess.ImgAfterDeskewingWithoutBorder(inputFile,1)
                                String finalImage = imageProcess.ImgAfterRemovingBackground(inputFile,1)

                                // configfileValue = 0->make the image visible, =1->make the image invisible
                                CreateSearchableImagePdf createPdf = new CreateSearchableImagePdf(finalImage
                                        , "./textonly_pdf_", "0")
                                createPdf.textOnlyPdf(finalImage,1)

                                println("getting the size and the location of the image from textonly_pdf_1")

                                Path path = Paths.get("textonly_pdf_1.pdf")
                                String ExistingPdfFilePath = path.toAbsolutePath().toString()
                                String outputFilePath1 = "newFile_1.pdf"
                                File outputFile = new File(generatedFilesPath.toString(), "${outputFilePath1}")
                                println(outputFile)

                                imageLocationsAndSize.createPdfWithOriginalImage(ExistingPdfFilePath,
                                        outputFile.toString(), imageNBorder)

                                /*
                                //Extract text from the image.
                                ImageText ocr = new ImageText(finalImage);
                                String fulltext = ocr.generateText();

                                System.out.println("Creating pdf document...");
                                String outputFileTextPath = "textExtracted.pdf"
                                File outputFileText = new File(generatedFilesPath.toString(), "${outputFileTextPath}")
                                TextPdf textpdf = new TextPdf(fulltext, outputFileText.toString());
                                System.out.println("Document created.");
                                textpdf.generateDocument(fulltext,1);
                                redirect(outputFileTextPath)
                                 */

                                redirect "/show/$outputFilePath1"
                            }


                })
            }
            get(":outputFilePath1"){
                FileService fileService ->
                parse(Form.class).then({ Form form ->
                    UploadedFile f = form.file("upload")
                    response.sendFile(fileService.get(pathTokens.outputFilePath1, f))
                })
            }

        }


/*
        get('file/:id'){
            File filePath = new File("${uploadPath}/${pathTokens['id']}")
            // you'd better check if the file exists...
            println("filePath: ${filePath}, exists: ${filePath.exists()}")
            render Paths.get(filePath.toURI())
        }
*/
        get('file/:id'){
            File filePath = new File("${generatedFilesPath}/${pathTokens['id']}")
            // you'd better check if the file exists...
            println("filePath: ${filePath}, exists: ${filePath.exists()}")
            render Paths.get(filePath.toURI())
        }

        get("show/:outputFilePath1"){
            String fileId = getPathTokens().get("outputFilePath1")
            String path = "/file/${fileId}"
            render( thymeleafTemplate("pdf", ['fullpath': path]) )

        }
        get("appear/:name"){
            String fileId = getPathTokens().get("outputFilePath1")
            String path = "/file/${fileId}"
            render( thymeleafTemplate("photo", ['fullpath': path]) )
        }

        get{
            String SearchablePDF = "Create a searchable pdf with invisible text layer"
            String Textoverlay = "Just extract and show overlay"
           def options = ['pdf':SearchablePDF,'text':Textoverlay ]
            render(thymeleafTemplate("index",options))
        }







    }
}

