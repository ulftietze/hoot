package hoot.app.Factory;

import hoot.system.Filesystem.FileHandler;
import hoot.system.objects.FactoryInterface;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;

public class FileHandlerFactory implements FactoryInterface<FileHandler>
{
    private final ServletContext context;

    public FileHandlerFactory(ServletContext context)
    {
        this.context = context;
    }

    @Override
    public FileHandler create()
    {
        try {
            String webroot = this.context.getResource(".").getPath();
            return new FileHandler(webroot);
        } catch (MalformedURLException e) {
            context.log(e.getMessage());
        }

        return null;
    }
}
