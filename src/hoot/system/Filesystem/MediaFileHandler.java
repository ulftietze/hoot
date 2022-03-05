package hoot.system.Filesystem;

import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Base64.Decoder;

public class MediaFileHandler
{
    public static String mediaPath = "media/";
    private final String webrootOnFilesystem;
    private final String contextUriPath;

    public MediaFileHandler(String webrootOnFilesystem, String contextUriPath)
    {
        this.webrootOnFilesystem = webrootOnFilesystem;
        this.contextUriPath      = contextUriPath;
    }

    /**
     * Decodes and saves types of media with Base64 in the designated media folder.
     *
     * @param mediaName    is the image-name with its format.
     * @param relativePath is the relative path to the media which is about to be saved.
     * @param base64E      is the media in Base64 encoded.
     */
    public void saveMedia(String mediaName, String relativePath, String base64E)
    {
        String fs        = File.separator;
        String imagePath = this.webrootOnFilesystem +  MediaFileHandler.mediaPath  + relativePath;
        File   directory = new File(imagePath);

        LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
        logger.log("imagepath: " + imagePath);

        directory.mkdirs();

        String[] parts       = base64E.split(",");
        String   imageString = parts[1];

        Decoder decoder   = Base64.getMimeDecoder();
        byte[]  imageByte = decoder.decode(imageString);

        try {
            Path path = Paths.get(imagePath + fs + mediaName);
            logger.log("TRY PATH: "+ path);
            Files.write(path, imageByte);
        } catch (IOException e) {

            logger.log("Saving of File " + imagePath + fs + mediaName + " failed: " + e.getMessage());
        }
    }

    /**
     * Deletes the specified media from the media folder.
     *
     * @param relativePath is the relative path to the media which is about to be deleted.
     */

    public void deleteMedia(String relativePath)
    {
        LoggerInterface l  = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
        String          fs = File.separator;

        String imagePath = this.webrootOnFilesystem + MediaFileHandler.mediaPath + fs + relativePath;
        File   file      = new File(imagePath);
        file.delete();
    }

    /**
     * Get the Url from a specific Image.
     *
     * @param relativePath is the relative Path to the Image.
     * @return The URL for the Image.
     */
    public String getImageUrl(String relativePath)
    {
        return this.contextUriPath + File.separator + MediaFileHandler.mediaPath + File.separator + relativePath;
    }
}
