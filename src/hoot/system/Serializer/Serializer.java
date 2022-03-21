package hoot.system.Serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hoot.system.objects.ObjectManager;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class Serializer
{
    private final ObjectMapper objectMapper;

    public Serializer()
    {
        this.objectMapper = (ObjectMapper) ObjectManager.get(ObjectMapper.class);
    }

    /**
     * @param request     The incoming http servlet request
     * @param targetClass The Output DTO from which the input body json is serialized
     * @return Object
     * @throws IOException Thrown while reading input stream
     */
    public Object deserializeJsonRequestBody(HttpServletRequest request, Class<?> targetClass) throws IOException
    {
        return this.objectMapper.readValue(request.getReader(), targetClass);
    }

    /**
     * TODO: Documentation
     *
     * @param json
     * @param targetClass
     * @return
     * @throws IOException
     */
    public Object deserialize(String json, Class<?> targetClass) throws IOException
    {
        return this.objectMapper.readValue(json, targetClass);
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
