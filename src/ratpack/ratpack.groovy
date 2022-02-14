import app.model.FileService
import app.services.DefaultFileService
import com.corposense.ocr.demo.ImageConverter
import com.corposense.ocr.demo.PdfCombiner
import com.corposense.ocr.demo.SearchableImagePdf
import com.corposense.ocr.demo.ExtractImage
import com.corposense.ocr.demo.ImageLocationsAndSize
import com.corposense.ocr.demo.ImageProcessing
import com.corposense.ocr.demo.ImageText
import com.corposense.ocr.demo.PdfConverter

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

String uploadDir = 'uploads'
String publicDir = 'public'
String generatedFilesDir = "generatedFiles"
String createdFilesDir = "createdFiles"

Path baseDir = BaseDir.find("${publicDir}/${uploadDir}")
Path baseGeneratedFilesDir = BaseDir.find("${publicDir}/${generatedFilesDir}")
Path baseCreatedFilesDir = BaseDir.find("${publicDir}/${generatedFilesDir}/${createdFilesDir}")

Path generatedFilesPath = baseGeneratedFilesDir.resolve(generatedFilesDir)
Path uploadPath = baseDir.resolve(uploadDir)
Path createdFilesPath = baseCreatedFilesDir.resolve(createdFilesDir)


ratpack {
    serverConfig {
        props("application.properties")
        maxContentLength(26214400)
    }
    bindings {
        module(ThymeleafModule)
        bind(FileService, DefaultFileService)
        bind(ImageLocationsAndSize)
        bind(Utils)
        bind(SearchableImagePdf)
        bind(ImageProcessing)
        bind(ExtractImage)
        bind(ImageText)
        bind(TextPdf)
        bind(PdfConverter)
        bind(PdfCombiner)
        bind(ImageConverter)
    }
    handlers {
        prefix("upload"){
            post {
                SearchableImagePdf searchableImagePdf,
                ImageLocationsAndSize imageLocationsAndSize, ImageProcessing imageProcess,
                ExtractImage extractImage,
                ImageText imagetext,
                TextPdf textPdf,
                PdfConverter pdfConverter,
                PdfCombiner pdfCombiner,
                ImageConverter imageConverter,

                FileService fileService->

                    parse(Form.class).then({ Form form ->
                        UploadedFile f = form.file("upload")
                        String options = form.get('options')

                        String name = fileService.save(f, "${uploadPath}")
                        File inputfilePath = new File("${uploadPath}/${name}")


                              if (fileService.isPdfFile(f)) {
                                if (options == "SearchablePDF") {
                                    pdfConverter.produceSearchablePdf("${inputfilePath}")
                                    String outputFilePath = "mergedImgPdf.pdf"
                                    File mergedFiles = new File("${generatedFilesPath}", "${outputFilePath}")
                                    PdfCombiner.mergePdfDocuments("${inputfilePath}" ,"newFile_pdf_",
                                            "${mergedFiles}")

                                    fileService.deleteFiles("${createdFilesPath}")
                                    redirect "/show/$outputFilePath/$name"

                                }
                                if(options == "Textoverlay"){
                                    pdfConverter.produceTextOverlay("${inputfilePath}")
                                    String outputFilePath = "mergedText.pdf"
                                    File outputFile = new File("${generatedFilesPath}", "${outputFilePath}")
                                    PdfCombiner.mergePdfDocuments("${inputfilePath}", "ocrDemo_pdf_"
                                            ,"${outputFile}")

                                    fileService.deleteFiles("${createdFilesPath}")
                                    redirect "/show/$outputFilePath/$name"
                                }

                                  // Dealing with Images
                            } else {
                                if (options == "SearchablePDF") {

                                    String imageNBorder = imageConverter.createTextOnlyPdf("${inputfilePath}")
                                    String outputFilePath = "newFile_1.pdf"
                                    String pdfFile = imageLocationsAndSize.createPdfWithOriginalImage("textonly_pdf_1.pdf",
                                            outputFilePath , imageNBorder)

                                    fileService.moveFile("${generatedFilesPath}",pdfFile)
                                    fileService.deleteFiles("${createdFilesPath}")

                                    redirect "/appear/$outputFilePath/$name"
                                }
                                 if(options == "Textoverlay"){
                                     String fulltext = imageConverter.produceText("${inputfilePath}")
                                     String outputFilePath = "ocrDemo_1.pdf"
                                     TextPdf textpdf = new TextPdf(fulltext, outputFilePath)
                                     String doc = textpdf.generateDocument(fulltext, 1)

                                     fileService.moveFile("${generatedFilesPath}",doc)
                                     fileService.deleteFiles("${createdFilesPath}")

                                    redirect "/appear/$outputFilePath/$name"
                                }
                            }
                })
            }
            get(":outputFilePath"){
                FileService fileService ->
                    response.sendFile(fileService.get(pathTokens.outputFilePath))
            }

            get(":name"){
                FileService fileService ->
                    response.sendFile(fileService.get(pathTokens.name))
            }

        }

        get('name/:id'){
            File filePath = new File("${uploadPath}/${pathTokens['id']}")
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
            LinkedHashMap options = ['pdf':SearchablePDF,'text':Textoverlay ]
            render(thymeleafTemplate("index",options))
        }

    }
}

