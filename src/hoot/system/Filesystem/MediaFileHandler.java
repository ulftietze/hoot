package hoot.system.Filesystem;

import java.io.*;
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
     * TODO: Documentation
     *
     * @param mediaName
     * @param relativePath
     * @param base64E
     */
    public void saveMedia(String mediaName, String relativePath, String base64E)
    {
        String fs        = File.separator;
        String imagePath = this.webrootOnFilesystem + fs + MediaFileHandler.mediaPath + fs + relativePath;
        File   directory = new File(imagePath);
        File   file      = new File(imagePath + mediaName);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String[] parts       = base64E.split(",");
        String   imageString = parts[1];

        Decoder decoder   = Base64.getDecoder();
        byte[]  imageByte = decoder.decode(imageString);

        try {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
            outputStream.write(imageByte);
        } catch (IOException ignored) {
        }
    }

    /**
     * TODO: Documentation
     *
     * @param relativePath
     * @return
     */
    public String getImageUrl(String relativePath)
    {
        return this.contextUriPath + "/" + MediaFileHandler.mediaPath + "/" + relativePath;
    }
}
