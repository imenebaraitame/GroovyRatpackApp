package app.services

import app.model.FileService
import ratpack.form.UploadedFile
import java.nio.file.Files
import java.nio.file.Path

class DefaultFileService implements FileService {

    private static final prefix = "ratpack-"
    final Path tmpDir = File.createTempDir().toPath()

    @Override
    String save(UploadedFile f, String uploadPath) {
        String suffix = getSuffix(f)
        Path dest = Files.createTempFile(tmpDir, prefix, suffix)
        String fileId = dest.fileName.toString().replaceAll("^${prefix}", "")

        Path unixPath = Files.write(dest, f.bytes)
        println("unixPath: ${unixPath}, fileId: ${fileId}")
        File outputFile = new File(uploadPath, "${fileId}")


        f.writeTo(outputFile.newOutputStream())
        println("outputFile: ${outputFile.path}, Exists: ${outputFile.exists()}")

        return fileId
    }

    @Override
    Path get(String name) {
        tmpDir.resolve getFileName(name)
    }

    @Override
    String getSuffix(UploadedFile f){
        int dot = f.getFileName().lastIndexOf(".")
        return f.getFileName().substring(dot)
    }

    @Override
    Boolean isPdfFile( UploadedFile file ) {
        file.contentType.type.contains( "application/pdf" )
    }

    private static String getFileName(String name) {
       return "${prefix}${name}"
    }

}
