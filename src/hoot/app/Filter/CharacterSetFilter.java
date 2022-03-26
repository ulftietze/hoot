package hoot.app.Filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "CharacterSetFilter", urlPatterns = {"/*"})
public class CharacterSetFilter implements Filter
{
    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain next
    ) throws IOException, ServletException
    {
        var httpRequest  = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;

        httpRequest.setCharacterEncoding("UTF-8");
        httpResponse.setCharacterEncoding("UTF-8");
        next.doFilter(request, response);
    }
}
