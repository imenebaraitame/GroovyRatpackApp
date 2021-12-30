import app.model.FileService
import app.services.DefaultFileService
import com.corposense.ocr.demo.CreateSearchableImagePdf

import com.corposense.ocr.demo.ImageLocationsAndSize
import com.corposense.ocr.demo.ImageProcess
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
    }
    handlers {
        prefix("upload"){
            post {
                CreateSearchableImagePdf createSearchableImagePdf,
                ImageLocationsAndSize imageLocationsAndSize,ImageProcess imageProcess,
                FileService fileService->
                    parse(Form.class).then({ Form form ->
                        UploadedFile f = form.file("upload")
                        String name = fileService.save(f, uploadPath.toString())

                            File filePath = new File("${uploadPath}/${name}")
                            String imageNBorder = imageProcess.ImgAfterDeskewingWithoutBorder(filePath.toString())
                            String finalImage = imageProcess.ImgAfterRemovingBackground(filePath.toString())
                            CreateSearchableImagePdf createPdf = new CreateSearchableImagePdf(finalImage
                                    , "./textonly_pdf", "0")
                            createPdf.textOnlyPdf(finalImage)

                            println("getting the size and the location of the image from textonly_pdf")

                            Path path = Paths.get("textonly_pdf.pdf")
                            String ExistingPdfFilePath = path.toAbsolutePath().toString()
                            String outputFilePath = "newFile.pdf"
                            File outputFile = new File(generatedFilesPath.toString(),"${outputFilePath}")
                            println(outputFile)

                            imageLocationsAndSize.createPdfWithOriginalImage(ExistingPdfFilePath,
                                    outputFile.toString(), imageNBorder)


                        //String contentType = context.get(MimeTypes).getContentType(name)
                     // if(contentType.contains("application/pdf"))
                      // {
                           redirect "/show/$outputFilePath"
                      // }else{
                       //   redirect "/appear/$name"
                       //}

                })
            }
            get(":outputFilePath"){
                FileService fileService ->
                parse(Form.class).then({ Form form ->
                    UploadedFile f = form.file("upload")
                    response.sendFile(fileService.get(pathTokens.outputFilePath, f))
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

        get("show/:outputFilePath"){
            String fileId = getPathTokens().get("outputFilePath")
            String path = "/file/${fileId}"
            render( thymeleafTemplate("pdf", ['fullpath': path]) )

        }
        get("appear/:name"){
            String fileId = getPathTokens().get("outputFilePath")
            String path = "/file/${fileId}"
            render( thymeleafTemplate("photo", ['fullpath': path]) )
        }

        files { dir "public" indexFiles 'index.html' }

    }
}