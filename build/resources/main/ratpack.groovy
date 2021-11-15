import app.model.PhotoService
import app.services.DefaultPhotoService
import ratpack.form.Form
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
<<<<<<< HEAD
            name:getPathTokens().get("name")
            render thymeleafTemplate("photo", )
=======
              render thymeleafTemplate("photo")
               getPathTokens().get("name")
>>>>>>> 4b03b5b5d145580e3e38328c94180adaddcd8888
            }



        files { dir "public" indexFiles 'index.html' }


    }
}
