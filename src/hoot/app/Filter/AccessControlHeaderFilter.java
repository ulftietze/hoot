package hoot.app.Filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "AccessControlHeaderFilter", urlPatterns = {"/*"})
public class AccessControlHeaderFilter implements Filter
{
    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain next
    ) throws IOException, ServletException
    {
        var httpRequest  = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;

        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
        httpResponse.setHeader(
                "Access-Control-Allow-Headers",
                "Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers"
        );

        next.doFilter(request, response);
    }
}
