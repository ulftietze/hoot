package hbv.Filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "TestFilter", urlPatterns = {"/*"}, initParams = {@WebInitParam(name = "moin", value = "tach auch")})
public class MyFilter implements Filter
{
    ServletContext ctx;

    public void init(FilterConfig config) throws ServletException
    {
        ctx = config.getServletContext();
    }

    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain chain
    ) throws IOException, ServletException
    {
        HttpServletRequest  req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        //res.sendRedirect(req.getContextPath() + "/");

        String forwardedFor = req.getHeader("X-Forwarded-For");
        String requestURL   = "" + req.getRequestURL();
        ctx.log(ctx.getContextPath() + ".IP " + forwardedFor);

        chain.doFilter(request, response);
    }

    public void destroy() {}
}
