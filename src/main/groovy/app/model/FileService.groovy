package app.model

import ratpack.api.NonBlocking
import ratpack.form.UploadedFile
import java.nio.file.Path

/**
 *An interface for working with the uploaded photos
 */

interface FileService {
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

    Path get(String name)

    /**
     * get the suffix of a file
     *
     * @param f
     * @return the suffix
     */

    String getSuffix(UploadedFile f)

    /**
     * check wether the uploaded file is image or not
     *
     * @param f
     */
    Boolean isPdfFile(UploadedFile f)

    /**
     * delete created files
     * @param filePath
     */
    void deleteFiles(String filePath)
    /**
     * move file from directory to another
     * @param dirPath
     * @param pdfPath
     */
    void moveFile(String dirPath , String pdfPath)

}