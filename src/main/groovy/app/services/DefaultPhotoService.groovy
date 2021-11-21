package app.services

import app.model.PhotoService
import ratpack.form.UploadedFile
import java.nio.file.Files
import java.nio.file.Path

class DefaultPhotoService implements PhotoService {

    private static final PREFIX = "ratpack-"
    private static final SUFFIX = ".png"
    final Path tmpDir = File.createTempDir().toPath()

    @Override
    String save(UploadedFile f, String uploadPath) {
        Path dest = Files.createTempFile(tmpDir, PREFIX, SUFFIX)
        String fileId = dest.fileName.toString().replaceAll("^${PREFIX}", "").replaceAll("${SUFFIX}\$", "")
        def unixPath = Files.write(dest, f.bytes)
        println("unixPath: ${unixPath}, fileId: ${fileId}")

        File outputFile = new File(uploadPath, "${fileId}${SUFFIX}")
        f.writeTo(outputFile.newOutputStream())        
        println("outputFile: ${outputFile.path}, Exists: ${outputFile.exists()}")
        return fileId
    }

    @Override
    Path get(String name) {
        tmpDir.resolve getFileName(name)
    }

    private static String getFileName( String name) {
        "${PREFIX}${name}${SUFFIX}"
    }
}
