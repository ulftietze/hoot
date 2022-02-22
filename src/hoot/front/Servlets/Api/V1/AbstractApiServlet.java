package hoot.front.Servlets.Api.V1;

import hoot.system.Serializer.RequestSerializer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public abstract class AbstractApiServlet extends HttpServlet
{
    private RequestSerializer requestSerializer;

    /**
     * @param request   The incoming http servlet request
     * @param targetDTO The Output DTO from which the input body json is serialized
     * @return Object
     * @throws IOException Thrown while reading input stream
     */
    protected Object deserializeJsonRequestBody(HttpServletRequest request, Class<?> targetDTO) throws IOException
    {
        return this.getRequestSerializer().deserializeJsonRequestBody(request, targetDTO);
    }

    /**
     * @param toSerialize The DTO to Serialize
     * @return the serialized json string
     */
    protected String serializeJsonResponseBody(Object toSerialize)
    {
        return this.getRequestSerializer().serializeJsonResponseBody(toSerialize);
    }

    private RequestSerializer getRequestSerializer()
    {
        if (this.requestSerializer == null) {
            this.requestSerializer = new RequestSerializer();
        }

        return requestSerializer;
    }
}
