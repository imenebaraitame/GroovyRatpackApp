package app.model

import ratpack.api.NonBlocking
import ratpack.form.UploadedFile
import java.nio.file.Path

/**
 *An interface for working with the uploaded photos
 */

interface PhotoService {
/**
 * Save the file and return the file name
 *
 * @param f
 * @return the file name
 */
@NonBlocking
    String save(UploadedFile f, String uploadPath)

/**
 * Retrieves a file by name
 *
 * @param name
 * @return the file
 */

    Path get(String name, UploadedFile f)

    /**
     * get the suffix of a file
     *
     * @param f
     * @return the suffix
     */

    String SUFFIX(UploadedFile f)


    
}