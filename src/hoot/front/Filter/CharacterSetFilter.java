package hoot.front.Filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(filterName = "CharacterSetFilter", urlPatterns = {"/*"})
public class CharacterSetFilter implements Filter
{
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException, ServletException
    {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        next.doFilter(request, response);
    }
}
