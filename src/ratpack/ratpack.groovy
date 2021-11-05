import app.model.PhotoService
import app.services.DefaultPhotoService
import ratpack.form.Form
import ratpack.thymeleaf3.ThymeleafModule
import static ratpack.thymeleaf3.Template.thymeleafTemplate
import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        module (ThymeleafModule)
        bind PhotoService, DefaultPhotoService
    }
    handlers {
        prefix("photo"){
            post {
                PhotoService photoService ->
                    def form = parse(Form)
                    def name = photoService.save(form.file("photo"))
                    redirect "/show/$name"
            }
            get(":name"){
                PhotoService photoService ->
                    response.sendFile context, photoService.get(pathTokens.name)
            }
        }
        get("show/:name"){
            render thymeleafTemplate("photo.html", name:pathTokens.name)
        }
        assets "public"


    }
}
