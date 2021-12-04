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
        String ext = getSuffix(f)
        Path dest = Files.createTempFile(tmpDir, PREFIX, ext)
        String fileId = dest.fileName.toString().replaceAll("^${PREFIX}", "").replaceAll("${ext}\$", "")
        def unixPath = Files.write(dest, f.bytes)
        println("unixPath: ${unixPath}, fileId: ${fileId}")

        File outputFile = new File(uploadPath, "${fileId}${ext}")
        f.writeTo(outputFile.newOutputStream())
        println("outputFile: ${outputFile.path}, Exists: ${outputFile.exists()}")
        return fileId
    }


    @Override
    Path get(String name,UploadedFile f) {
        tmpDir.resolve getFileName(name,f)
    }

    @Override
    String getSuffix(UploadedFile f){
        int dot = f.getFileName().lastIndexOf(".")
        return f.getFileName().substring(dot)
    }

    private static String getFileName(String name,UploadedFile f) {
       return "${PREFIX}${name}${getSuffix(f)}"
    }

}
