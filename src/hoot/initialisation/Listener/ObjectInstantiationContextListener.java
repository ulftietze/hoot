package hoot.initialisation.Listener;

import hoot.model.mapper.RegisterDtoToUserMapper;
import hoot.model.mapper.UserToUserDtoMapper;
import hoot.model.mapper.hoot.CommentDtoToCommentMapper;
import hoot.model.mapper.hoot.ImageDtoToImageMapper;
import hoot.model.mapper.hoot.PostDtoToPostMapper;
import hoot.model.query.GetStringHashed;
import hoot.model.query.api.GetUserIdIfValidLogin;
import hoot.model.query.api.IsAuthenticationRequired;
import hoot.model.query.api.IsValidUserSession;
import hoot.model.repositories.HootRepository;
import hoot.model.repositories.UserRepository;
import hoot.model.search.HootSearchCriteria;
import hoot.model.search.UserSearchCriteria;
import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.Logger.ContextLogger;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Serializer.RequestSerializer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.util.Timer;

public class ObjectInstantiationContextListener implements ServletContextListener
{
    Timer timer;

    /**
     * TODO: Breaking We need to check if the classes have dependencies to each other.
     * TODO: Maybe lazy load objects?
     */
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        ServletContext context = servletContextEvent.getServletContext();

        // System
        ObjectManager.set(DataSource.class, this.getDataSource(context));
        ObjectManager.set(RequestSerializer.class, new RequestSerializer());
        ObjectManager.set(LoggerInterface.class, new ContextLogger(context));
        ObjectManager.set(MediaFileHandler.class, this.getImageFileHandler(context));

        // Repositories
        ObjectManager.set(UserRepository.class, new UserRepository());
        ObjectManager.set(HootRepository.class, new HootRepository());

        // SearchCriteria
        ObjectManager.set(HootSearchCriteria.class, new HootSearchCriteria());
        ObjectManager.set(UserSearchCriteria.class, new UserSearchCriteria());

        // Queries
        ObjectManager.set(GetStringHashed.class, new GetStringHashed());
        ObjectManager.set(GetUserIdIfValidLogin.class, new GetUserIdIfValidLogin());
        ObjectManager.set(IsAuthenticationRequired.class, new IsAuthenticationRequired());
        ObjectManager.set(IsValidUserSession.class, new IsValidUserSession());

        // Mapper
        ObjectManager.set(RegisterDtoToUserMapper.class, new RegisterDtoToUserMapper());
        ObjectManager.set(UserToUserDtoMapper.class, new UserToUserDtoMapper());
        ObjectManager.set(CommentDtoToCommentMapper.class, new CommentDtoToCommentMapper());
        ObjectManager.set(ImageDtoToImageMapper.class, new ImageDtoToImageMapper());
        ObjectManager.set(PostDtoToPostMapper.class, new PostDtoToPostMapper());
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
    }

    private DataSource getDataSource(ServletContext context)
    {
        try {
            Context initCtx = new InitialContext();
            Context envCtx  = (Context) initCtx.lookup("java:/comp/env");
            return (DataSource) envCtx.lookup("jdbc/mariadb");
        } catch (NamingException e) {
            context.log(e.getMessage());
        }

        return null;
    }

    private MediaFileHandler getImageFileHandler(ServletContext context)
    {
        try {
            String webroot = context.getResource(".").getPath();
            new MediaFileHandler(webroot, context.getContextPath());
        } catch (MalformedURLException e) {
            context.log(e.getMessage());
        }

        return null;
    }
}
