import app.model.FileService
import app.services.DefaultFileService
import ratpack.form.Form
import ratpack.thymeleaf3.ThymeleafModule

import static ratpack.thymeleaf3.Template.thymeleafTemplate
import static ratpack.groovy.Groovy.ratpack
import ratpack.server.BaseDir
import java.nio.file.Paths
import ratpack.file.MimeTypes

def uploadDir = 'uploads'
def publicDir = 'public'
def baseDir = BaseDir.find("${publicDir}/${uploadDir}")
//def baseDir = BaseDir.findBaseDir()
//def baseDir = BaseDir.find(".")

def uploadPath = baseDir.resolve(uploadDir)
//def uploadPath = baseDir.getRoot().resolve(uploadDir)
//def uploadPath = baseDir.getRoot().resolve("${publicDir}/${uploadDir}")


ratpack {
    bindings {
        module (ThymeleafModule)
        bind (FileService, DefaultFileService)
    }
    handlers {
        prefix("upload"){
            post {
                FileService fileService ->
                    parse(Form.class).then({ def form ->
                    def f = form.file("upload")
                    def name = fileService.save(f, uploadPath.toString())
                        def contentType = context.get(MimeTypes).getContentType(name)

                      if(contentType.contains("application/pdf"))
                       {
                           redirect "/show/$name"
                       }else{
                          redirect "/appear/$name"
                       }

                })
            }
            get(":name"){
                FileService fileService ->
                parse(Form.class).then({ def form ->
                    def f = form.file("upload")
                    response.sendFile(fileService.get(pathTokens.name, f))
                })
            }
        }

        get('file/:id'){
            def filePath = new File("${uploadPath}/${pathTokens['id']}")
            // you'd better check if the file exists...
            println("filePath: ${filePath}, exists: ${filePath.exists()}")
            render Paths.get(filePath.toURI())
        }

        get("show/:name"){
            String fileId = getPathTokens().get("name")
            String path = "/file/${fileId}"
            render( thymeleafTemplate("pdf", ['fullpath': path]) )

        }
        get("appear/:name"){
            String fileId = getPathTokens().get("name")
            String path = "/file/${fileId}"
            render( thymeleafTemplate("photo", ['fullpath': path]) )
        }

        files { dir "public" indexFiles 'index.html' }

    }
}