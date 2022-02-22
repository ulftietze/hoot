package hoot.front.Filter;

import hoot.model.query.api.IsAuthenticationRequired;
import hoot.model.query.api.IsValidUserSession;
import hoot.system.Serializer.RequestSerializer;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"/api/V1/*"})
public class AuthorizationFilter implements Filter
{
    private RequestSerializer requestSerializer;

    private IsAuthenticationRequired isAuthenticationRequired;

    private IsValidUserSession isValidUserSession;

    public void init(FilterConfig config)
    {
        this.requestSerializer        = new RequestSerializer();
        this.isValidUserSession       = new IsValidUserSession();
        this.isAuthenticationRequired = new IsAuthenticationRequired();
    }

    /**
     * @param request  Servlet requests. Will probably always be HttpServletRequest
     * @param response Servlet response. Will probably always be HttpServletResponse
     * @param chain    The FilterChain to invoke the next filter in the chain - or if the last filter to invoke the
     *                 resource at the end of the chain - therefore the matching servlet.
     */
    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain chain
    ) throws IOException, ServletException
    {
        HttpServletRequest  httpRequest  = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (this.isUnauthorized(httpRequest)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().print(this.requestSerializer.serializeJsonResponseBody("Unauthorized"));
            return;
        }

        chain.doFilter(request, response);
    }

    public void destroy() {}

    private boolean isUnauthorized(HttpServletRequest httpRequest) throws ServletException
    {
        String servletName = httpRequest.getHttpServletMapping().getServletName();
        String httpMethod  = httpRequest.getMethod();

        return this.isAuthenticationRequired.execute(servletName, httpMethod) && !this.isValidUserSession.execute(
                httpRequest.getSession());
    }
}