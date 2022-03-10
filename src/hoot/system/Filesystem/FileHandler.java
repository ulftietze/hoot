package hoot.system.Filesystem;

import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileHandler
{
    private final String          webrootOnFilesystem;
    private final LoggerInterface logger;

    public FileHandler(String webrootOnFilesystem)
    {
        this.webrootOnFilesystem = webrootOnFilesystem;
        this.logger              = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    /**
     * Save text content to a file and return the absolute path.
     *
     * @param content      text content, each line represented by a new entry in the ArrayList
     * @param relativePath e.g. path + File.separator + to + File.separator + save
     * @param filename     e.g. textfile.txt
     * @return The absolute path the object was saved to
     */
    public String save(ArrayList<String> content, String relativePath, String filename)
    {
        String directoryPath = this.webrootOnFilesystem + File.separator + relativePath;
        Path   absolutePath  = Paths.get(directoryPath + File.separator + filename);
        File   directory     = new File(directoryPath);

        directory.mkdirs();

        try {
            Files.write(absolutePath, content);
            return absolutePath.toString();
        } catch (IOException e) {
            this.logger.log("Saving of File " + absolutePath + " failed: " + e.getMessage());
        }

        return null;
    }

    public void delete(String relativePath, String filename)
    {
        String directoryPath = this.webrootOnFilesystem + File.separator + relativePath;
        Path   absolutePath  = Paths.get(directoryPath + File.separator + filename);
        try {
            Files.delete(absolutePath);
        } catch (IOException e) {
            this.logger.log("Deleting of File " + absolutePath + " failed: " + e.getMessage());
        }
    }
}
