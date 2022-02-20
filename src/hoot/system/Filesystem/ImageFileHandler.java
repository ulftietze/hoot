package hoot.system.Filesystem;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Base64;
import java.util.Base64.Decoder;

public class ImageFileHandler
{
    public static String mediaPath = "static/media/";
    private final String webRoot;

    public ImageFileHandler(String webRoot)
    {
        this.webRoot = "webapps" + webRoot + File.separator + ImageFileHandler.mediaPath;
    }

    public void saveImage(String imageName, String relativePath, String base64E, ServletContext ctx)
    {
        File directory = new File(this.webRoot);
        File file      = new File(this.webRoot + imageName);

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
        } catch (IOException e) {
            e.printStackTrace();
            ctx.log(e.getMessage());
        }

        ctx.log(DirectoryTree.printDirectoryTree(new File(".")));
    }

    public String getImageUrl(String relativePath)
    {
        // TODO: Locate file in System
        return "/docker-wasauchimmer-java" + mediaPath + relativePath;
    }
}
