package hoot.system.Serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequestSerializer
{
    private final ObjectMapper objectMapper;

    public RequestSerializer()
    {
        this.objectMapper = (ObjectMapper) ObjectManager.get(ObjectMapper.class);
    }

    /**
     * @param request   The incoming http servlet request
     * @param targetDTO The Output DTO from which the input body json is serialized
     * @return Object
     * @throws IOException Thrown while reading input stream
     */
    public Object deserializeJsonRequestBody(HttpServletRequest request, Class<?> targetDTO) throws IOException
    {
        return this.objectMapper.readValue(request.getReader(), targetDTO);
    }

    /**
     * @param toSerialize The DTO to Serialize
     * @return the serialized json string
     */
    public String serialize(Object toSerialize)
    {
        try {
            return this.objectMapper.writeValueAsString(toSerialize);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not serialize object: " + e.getMessage());
        }
    }
}
