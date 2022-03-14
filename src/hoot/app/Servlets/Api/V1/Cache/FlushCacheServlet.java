package hoot.app.Servlets.Api.V1.Cache;

import hoot.app.Servlets.Api.V1.AbstractApiServlet;
import hoot.system.Cache.CacheManager;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/V1/cache/flush")
public class FlushCacheServlet extends AbstractApiServlet
{
    private CacheManager cacheManager;

    @Override
    public void init() throws ServletException
    {
        super.init();

        this.cacheManager = (CacheManager) ObjectManager.get(CacheManager.class);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.cacheManager.flushCache();
        this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize("flushed"));
    }
}
