package hoot.front.Servlets.Api.V1;

import com.google.gson.Gson;
import hbv.Exceptions.UnauthorizedException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;

public abstract class AbstractApiServlet extends HttpServlet
{
    /**
     * @param request   The incoming http servlet request
     * @param targetDTO The Output DTO from which the input body json is serialized
     * @return Object
     * @throws IOException Thrown while reading input stream
     */
    protected Object deserializeJsonRequestBody(HttpServletRequest request, Class<?> targetDTO) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();
        String        line          = null;

        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return new Gson().fromJson(stringBuilder.toString(), targetDTO);
    }

    /**
     * @param toSerialize The DTO to Serialize
     * @return the serialized json string
     */
    protected String serializeJsonResponseBody(Object toSerialize)
    {
        return new Gson().toJson(toSerialize);
    }

    protected void validateAuthentication(HttpServletRequest request) throws UnauthorizedException
    {
        HttpSession session  = request.getSession(true);
        boolean     loggedIn = false;

        if (session.getAttribute("user-id") != null) {
            return;
        }

        throw new UnauthorizedException("User is not allowed to access this resource");
    }
}
