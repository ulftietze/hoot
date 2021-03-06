package hoot.app.Filter;

import hoot.model.query.api.IsAuthenticationRequired;
import hoot.model.query.api.IsValidUserSession;
import hoot.model.queue.publisher.HttpRequestPublisher;
import hoot.system.objects.ObjectManager;
import hoot.system.Serializer.Serializer;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"/*"})
public class AuthorizationFilter implements Filter
{
    private Serializer serializer;

    private IsAuthenticationRequired isAuthenticationRequired;

    private IsValidUserSession isValidUserSession;

    private HttpRequestPublisher requestPublisher;

    public void init(FilterConfig config)
    {
        this.serializer               = (Serializer) ObjectManager.get(Serializer.class);
        this.isValidUserSession       = (IsValidUserSession) ObjectManager.get(IsValidUserSession.class);
        this.isAuthenticationRequired = (IsAuthenticationRequired) ObjectManager.get(IsAuthenticationRequired.class);
        this.requestPublisher         = (HttpRequestPublisher) ObjectManager.get(HttpRequestPublisher.class);
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

        // TODO: Move to a dedicated place
        this.requestPublisher.publish(new HashMap<String, Object>()
        {{
            put("session", httpRequest.getSession(false));
            put("requestURI", httpRequest.getRequestURI());
        }});

        if (this.isUnauthorized(httpRequest)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().print(this.serializer.serialize("Unauthorized"));
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isUnauthorized(HttpServletRequest httpRequest) throws ServletException
    {
        String  servletName = httpRequest.getHttpServletMapping().getServletName();
        String  httpMethod  = httpRequest.getMethod();
        boolean isRequired  = this.isAuthenticationRequired.execute(servletName, httpMethod);

        return isRequired && !this.isValidUserSession.execute(httpRequest.getSession(false));
    }
}
