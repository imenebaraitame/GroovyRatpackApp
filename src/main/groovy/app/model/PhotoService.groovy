package app.model

import ratpack.form.UploadedFile

import java.nio.file.Path

interface PhotoService {

    String save(UploadedFile f)

    Path get(String name)

    
}