package app.services

import app.model.PhotoService
import ratpack.form.UploadedFile
import java.nio.file.Files
import java.nio.file.Path

class DefaultPhotoService implements PhotoService {

    private static final PREFIX = "ratpack-"
    final Path tmpDir = File.createTempDir().toPath()

    @Override
    String save(UploadedFile f, String uploadPath) {

        Path dest = Files.createTempFile(tmpDir, PREFIX,SUFFIX(f))
        String fileId = dest.fileName.toString().replaceAll("^${PREFIX}", "").replaceAll("${SUFFIX(f)}\$", "")
        def unixPath = Files.write(dest, f.bytes)
        println("unixPath: ${unixPath}, fileId: ${fileId}")

        File outputFile = new File(uploadPath, "${fileId}${SUFFIX(f)}")
        f.writeTo(outputFile.newOutputStream())
        println("outputFile: ${outputFile.path}, Exists: ${outputFile.exists()}")
        return fileId
    }


    @Override
    Path get(String name,UploadedFile f) {
        tmpDir.resolve getFileName(name,f)
    }

    @Override
    String SUFFIX(UploadedFile f){
        int dot = f.getFileName().lastIndexOf(".")
        String suffix = f.getFileName().substring(dot)
    }

    private static String getFileName( String name,UploadedFile f) {
       "${PREFIX}${name}${SUFFIX(f)}"
    }



}
