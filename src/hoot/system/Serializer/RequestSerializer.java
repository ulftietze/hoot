package hoot.system.Serializer;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class RequestSerializer
{
    Gson gson;

    public RequestSerializer()
    {
        this.gson = new Gson();
    }

    /**
     * @param request   The incoming http servlet request
     * @param targetDTO The Output DTO from which the input body json is serialized
     * @return Object
     * @throws IOException Thrown while reading input stream
     */
    public Object deserializeJsonRequestBody(HttpServletRequest request, Class<?> targetDTO) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();
        String        line          = null;

        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return this.gson.fromJson(stringBuilder.toString(), targetDTO);
    }

    /**
     * @param toSerialize The DTO to Serialize
     * @return the serialized json string
     */
    public String serializeJsonResponseBody(Object toSerialize)
    {
        return this.gson.toJson(toSerialize);
    }
}
