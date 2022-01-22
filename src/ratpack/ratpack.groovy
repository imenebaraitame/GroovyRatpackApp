import app.model.FileService
import app.services.DefaultFileService
import com.corposense.ocr.demo.SearchableImagePdf
import com.corposense.ocr.demo.ExtractImage
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
        module(ThymeleafModule)
        bind(FileService, DefaultFileService)
        bind(ImageLocationsAndSize)
        bind(Utils)
        bind(SearchableImagePdf)
        bind(ImageProcess)
        bind(ExtractImage)
        bind(ImageText)
        bind(TextPdf)
    }
    handlers {
        prefix("upload"){
            post {
                SearchableImagePdf searchableImagePdf,
                ImageLocationsAndSize imageLocationsAndSize, ImageProcess imageProcess,
                ExtractImage extractImage,
                ImageText imagetext,
                TextPdf textPdf,
                FileService fileService->
                    parse(Form.class).then({ Form form ->
                        UploadedFile f = form.file("upload")
                        String options = form.get('options')

                        String name = fileService.save(f, uploadPath.toString())
                        String contentType = context.get(MimeTypes).getContentType(name)
                        File filePath = new File("${uploadPath}/${name}")
                        String inputFile = filePath.toString()


                            if (contentType.contains("application/pdf")) {
                                if (options == "SearchablePDF") {
                                    int pageNum = ExtractImage.pdfPageNumber(inputFile)
                                    extractImage.takeImageFromPdf(inputFile)
                                    searchableImagePdf.createSearchablePdf(inputFile,pageNum)

                                    String outputFilePath = "mergedImgPdf.pdf"
                                    File outputFile1 = new File(generatedFilesPath.toString(), "${outputFilePath}")
                                    extractImage.mergePdfDocuments(inputFile, "./newFile_pdf_", outputFile1.toString())
                                    redirect "/show/$outputFilePath/$name"

                                }else{
                                    int pageNum = ExtractImage.pdfPageNumber(inputFile)
                                    extractImage.takeImageFromPdf(inputFile)
                                    TextPdf.createTextOverlay(inputFile, pageNum)
                                    String outputFilePath = "mergedText.pdf"
                                    File outputFile1 = new File(generatedFilesPath.toString(), "${outputFilePath}")
                                    extractImage.mergePdfDocuments(inputFile, "./ocrDemo_pdf_", outputFile1.toString())

                                    redirect "/show/$outputFilePath/$name"
                                }

                            }else {
                                if (options == "SearchablePDF") {
                                    String imageNBorder = imageProcess.ImgAfterDeskewingWithoutBorder(inputFile, 1)
                                    String finalImage = imageProcess.ImgAfterRemovingBackground(inputFile, 1)

                                    // configfileValue = 0->make the image visible, =1->make the image invisible
                                    SearchableImagePdf createPdf = new SearchableImagePdf(finalImage,
                                            "./textonly_pdf_", "0")
                                    createPdf.textOnlyPdf(finalImage, 1)

                                    println("getting the size and the location of the image from textonly_pdf_1")

                                    Path path = Paths.get("textonly_pdf_1.pdf")

                                    String ExistingPdfFilePath = path.toAbsolutePath().toString()
                                    String outputFilePath = "newFile_1.pdf"
                                    File outputFile = new File(generatedFilesPath.toString(), "${outputFilePath}")


                                    imageLocationsAndSize.createPdfWithOriginalImage(ExistingPdfFilePath,
                                            outputFile.toString(), imageNBorder)

                                    redirect "/appear/$outputFilePath/$name"
                                }else{
                                    String imageNBorder = imageProcess.ImgAfterDeskewingWithoutBorder(inputFile, 1)
                                    String finalImage = imageProcess.ImgAfterRemovingBackground(inputFile, 1)
                                    //Extract text from the image.
                                    ImageText ocr = new ImageText(finalImage)
                                    String fulltext = ocr.generateText()

                                    System.out.println("Creating pdf document...")
                                    String outputFilePath = "ocrDemo_1.pdf"
                                    File outputFile = new File(generatedFilesPath.toString(), "${outputFilePath}")
                                    TextPdf textpdf = new TextPdf(fulltext, outputFile.toString())

                                    System.out.println("Document created.")
                                    textpdf.generateDocument(fulltext, 1)
                                    redirect "/appear/$outputFilePath/$name"

                                }
                            }
                })
            }
            get(":outputFilePath"){
                FileService fileService ->
                parse(Form.class).then({ Form form ->
                    UploadedFile f = form.file("upload")
                    response.sendFile(fileService.get(pathTokens.outputFilePath, f))
                })
            }

            get(":name"){
                FileService fileService ->
                    parse(Form.class).then({ Form form ->
                        UploadedFile f = form.file("upload")
                        response.sendFile(fileService.get(pathTokens.name, f))
                    })
            }

        }

        get('name/:id1'){
            File filePath = new File("${uploadPath}/${pathTokens['id1']}")
            // you'd better check if the file exists...
            println("filePath: ${filePath}, exists: ${filePath.exists()}")
            render Paths.get(filePath.toURI())
        }

        get('file/:id'){
            File filePath = new File("${generatedFilesPath}/${pathTokens['id']}")
            // you'd better check if the file exists...
            println("filePath: ${filePath}, exists: ${filePath.exists()}")
            render Paths.get(filePath.toURI())
        }

        get("show/:outputFilePath/:name"){
            String fileId = getPathTokens().get("outputFilePath")
            String path = "/file/${fileId}"
            String fileId2 = getPathTokens().get("name")
            String path2 = "/name/${fileId2}"
            render( thymeleafTemplate("pdf", ['fullpath': path ,'fullpath2': path2]) )

        }

        get("appear/:outputFilePath/:name"){
            String fileId = getPathTokens().get("name")
            String path = "/name/${fileId}"
            String fileId2 = getPathTokens().get("outputFilePath")
            String path2 = "/file/${fileId2}"
            render( thymeleafTemplate("photo", ['fullpath': path ,'fullpath2': path2]) )
        }

        get{
            String SearchablePDF = "Create a searchable pdf with invisible text layer"
            String Textoverlay = "Just extract and show overlay"
           def options = ['pdf':SearchablePDF,'text':Textoverlay ]
            render(thymeleafTemplate("index",options))
        }

    }
}

