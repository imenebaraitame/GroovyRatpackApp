package app.services

import app.model.PhotoService
import ratpack.form.UploadedFile
import java.nio.file.Files
import java.nio.file.Path


class DefaultPhotoService implements PhotoService {
    private static final PREFIX = "ratpack-"
    private static final SUFFIX = ".jpg"
    final Path tmpDir = File.createTempDir().toPath()

    @Override
    String save(UploadedFile f) {
        Path dest = Files.createTempFile(tmpDir,PREFIX,SUFFIX)
        Files.write(dest,f.bytes)
        dest.getFileName().toString().replaceAll("^${PREFIX}", "").replaceAll("${SUFFIX}\$", "")
    }

    @Override
    Path get(String name) {
        tmpDir.resolve getFileName(name)
    }

    private static String getFileName( String name) {
        "${PREFIX}${name}${SUFFIX}"
    }
}
