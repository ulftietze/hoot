package hoot.system.Filesystem;

public class ImageFileHandler
{
    public static String mediaPath = "/media/";

    public void saveImage(String imageName, String relativePath, String base64E)
    {
        // TODO: Save
    }

    public String getImageUrl(String relativePath)
    {
        // TODO: Locate file in System
        return "/docker-wasauchimmer-java" + mediaPath + relativePath;
    }
}
