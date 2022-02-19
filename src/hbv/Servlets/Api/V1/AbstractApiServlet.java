package hbv.Servlets.Api.V1;

import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public abstract class AbstractApiServlet extends HttpServlet
{
    /**
     * @param request   The incoming http servlet request
     * @param targetDTO The Output DTO from which the input body json is serialized
     * @return Object
     * @throws IOException
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

    protected String serializeJsonResponseBody(Object toSerialize)
    {
        return new Gson().toJson(toSerialize);
    }
}
