package hoot.initialisation.Factory;

import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.ObjectManager.FactoryInterface;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;

public class MediaFileHandlerFactory implements FactoryInterface<MediaFileHandler>
{
    private final ServletContext context;

    public MediaFileHandlerFactory(ServletContext context)
    {
        this.context = context;
    }

    @Override
    public MediaFileHandler create()
    {
        try {
            String webroot = this.context.getResource(".").getPath();
            return new MediaFileHandler(webroot, context.getContextPath());
        } catch (MalformedURLException e) {
            context.log(e.getMessage());
        }

        return null;
    }
}
