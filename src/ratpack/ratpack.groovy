import app.model.PhotoService
import app.services.DefaultPhotoService
import ratpack.form.Form
import ratpack.path.PathTokens
import ratpack.thymeleaf3.ThymeleafModule
import static ratpack.thymeleaf3.Template.thymeleafTemplate
import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        module (ThymeleafModule)
        bind (PhotoService, DefaultPhotoService)

    }
    handlers {
        prefix("photo"){
            post {
                PhotoService photoService ->
                    parse(Form.class).then({ form ->
                    def name = photoService.save(form.file("photo"))
                    redirect "/show/$name"
                })

            }
            get(":name"){
               PhotoService photoService ->
                   response.sendFile(photoService.get(pathTokens.name))
            }
        }
        get("show/:name"){
            name:getPathTokens().get("name")
            render thymeleafTemplate("photo")

            }



        files { dir "public" indexFiles 'index.html' }


    }
}
