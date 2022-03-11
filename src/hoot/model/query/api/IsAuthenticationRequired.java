package hoot.model.query.api;

import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletException;

public class IsAuthenticationRequired
{
    private static final String METHOD_DELETE  = "DELETE";
    private static final String METHOD_HEAD    = "HEAD";
    private static final String METHOD_GET     = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST    = "POST";
    private static final String METHOD_PUT     = "PUT";
    private static final String METHOD_TRACE   = "TRACE";

    /**
     * Checks the given servlet class for required authentication.
     * The check is called hierarchical. A method annotation will always overcall the class annotation.
     * <p>
     * Examples:
     * Example 1:
     * Type:   Class@AuthenticationRequired = true && Method@AuthenticationRequired = false
     * Result: Authentication not required
     * Example 2:
     * Type:   Class@AuthenticationRequired = false && Method@AuthenticationRequired = true
     * Result: Authentication required
     * Example 3:
     * Type:   Class@AuthenticationRequired = true
     * Result: Authentication required
     *
     * @param servletName The servlet class name on which the authentication check is executed
     * @param httpMethod  Based on the http method we need to check for servlet.doGet(), servlet.doPost(), ...
     * @return Whether the Authentication is required or not.
     */
    public boolean execute(String servletName, String httpMethod) throws ServletException
    {
        if (servletName == null || servletName.equals("default")) {
            return false;
        }

        boolean authenticationRequired;
        String  servletMethod = this.getServletMethod(httpMethod);

        try {
            Class<?>               servlet          = Class.forName(servletName);
            AuthenticationRequired annotationClass  = servlet.getAnnotation(AuthenticationRequired.class);
            AuthenticationRequired annotationMethod = this.getAnnotationForMethod(servlet, servletMethod);

            authenticationRequired = this.isAuthRequired(annotationClass, annotationMethod);
        } catch (ClassNotFoundException e) {
            throw new ServletException("Servlet or Servlet Method for route does not exist.");
        }

        return authenticationRequired;
    }

    /**
     * @param annotationClass|null  If this is not null, the annotation is set on the class
     * @param annotationMethod|null If this is not null, the annotation is set on the method
     * @return if the authentication for class/method combination is required
     */
    private boolean isAuthRequired(AuthenticationRequired annotationClass, AuthenticationRequired annotationMethod)
    {
        boolean authenticationRequired = annotationClass != null && annotationClass.authenticationRequired();

        if (annotationMethod != null && annotationMethod.authenticationRequired()) {
            authenticationRequired = true;
        } else if (annotationMethod != null) {
            authenticationRequired = false;
        }

        return authenticationRequired;
    }

    /**
     * Load the annotation for the method.
     *
     * @param servlet The servlet class to check the annotation
     * @param method  The matching method name inside the servlet.
     * @return AuthenticationRequired|null If the method is not found return null.
     */
    private AuthenticationRequired getAnnotationForMethod(Class<?> servlet, String method)
    {
        try {
            return (method != null) ? servlet.getMethod(method).getAnnotation(AuthenticationRequired.class) : null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Map the HTTP-Method to the equivalent servlet api method.
     *
     * @param httpMethod The HTTP-Method like GET, PUT, POST, DELETE, ...
     * @return String|null Equivalent Servlet Method or null if the method could not be mapped.
     */
    private String getServletMethod(String httpMethod)
    {
        // TODO: Change to enhanced switch when the Java Version is increased >=15
        switch (httpMethod) {
            case IsAuthenticationRequired.METHOD_GET:
                return "doGet";
            case IsAuthenticationRequired.METHOD_POST:
                return "doPost";
            case IsAuthenticationRequired.METHOD_PUT:
                return "doPut";
            case IsAuthenticationRequired.METHOD_DELETE:
                return "doDelete";
            default:
                return null;
        }
    }
}
