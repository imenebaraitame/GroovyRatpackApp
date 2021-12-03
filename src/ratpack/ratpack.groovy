import app.model.PhotoService
import app.services.DefaultPhotoService
import ratpack.form.Form
import ratpack.thymeleaf3.ThymeleafModule
import static ratpack.thymeleaf3.Template.thymeleafTemplate
import static ratpack.groovy.Groovy.ratpack
import ratpack.server.BaseDir
import java.nio.file.Paths

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
        bind (PhotoService, DefaultPhotoService)

    }
    handlers {
        prefix("photo"){
            post {
                PhotoService photoService ->
                    
                    parse(Form.class).then({ def form ->
                    def f = form.file("photo")
                    def name = photoService.save(f, uploadPath.toString())
                        String suffix = photoService.SUFFIX(f)

                    redirect "/show/$name/$suffix"
                })

            }
            get(":name"){
               PhotoService photoService ->
                   parse(Form.class).then({ def form ->
                       def f = form.file("photo")

                       response.sendFile(photoService.get(pathTokens.name, f))
            })
            }
        }

        get('image/:id'){
            def imagePath = new File("${uploadPath}/${pathTokens['id']}")
            // you'd better check if the image exists...
            println("imagePath: ${imagePath}, exists: ${imagePath.exists()}")
            render Paths.get(imagePath.toURI())
        }

        get("show/:name/:suffix"){
            String fileId = getPathTokens().get("name")
            String SU = getPathTokens().get("suffix")

            if(SU.contains(".jpg"))
            {
                String path = "/image/${fileId}${SU}"
                render( thymeleafTemplate("photo", ['fullpath': path]) )
            } else{
                //String pathpdf = "/image/${fileId}${SU}"
                //render( thymeleafTemplate("photo",[fullpathpdf:pathpdf]) )
                render(thymeleafTemplate("/pdfjs-2.10.377-dist/web/viewer.html"))

            }

        }

        files { dir "public" indexFiles 'index.html' }

    }
}