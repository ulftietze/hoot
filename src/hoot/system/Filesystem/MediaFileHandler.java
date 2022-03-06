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
     * @param mediaName    is the mediaName with its format.
     * @param relativePath is the relative path without the mediaName.
     * @param base64E      is the media in Base64 encoded.
     */
    public void saveMedia(String mediaName, String relativePath, String base64E)
    {
        LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);

        String imagePath;
        File   directory;
        if (relativePath != null) {
            imagePath = this.webrootOnFilesystem + MediaFileHandler.mediaPath + relativePath;
            directory = new File(imagePath);
            directory.mkdirs();
        } else {
            imagePath = this.webrootOnFilesystem + MediaFileHandler.mediaPath;
        }

        String[] parts       = base64E.split(",");
        String   imageString = parts[1];

        Decoder decoder   = Base64.getMimeDecoder();
        byte[]  imageByte = decoder.decode(imageString);

        try {
            Path path = Paths.get(imagePath + mediaName);
            Files.write(path, imageByte);
        } catch (IOException e) {

            logger.log("Saving of File " + imagePath + mediaName + " failed: " + e.getMessage());
        }
    }

    /**
     * Deletes the specified media from the media folder.
     *
     * @param relativePath is the relative path to the media with the filename which is about to be deleted.
     */

    public void deleteMedia(String relativePath)
    {

        String imagePath = this.webrootOnFilesystem + MediaFileHandler.mediaPath + relativePath;
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
