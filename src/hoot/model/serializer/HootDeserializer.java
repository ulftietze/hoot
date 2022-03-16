package hoot.model.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import hoot.model.entities.*;

import java.io.IOException;

public class HootDeserializer extends StdDeserializer<Hoot>
{
    public HootDeserializer()
    {
        this(null);
    }

    public HootDeserializer(Class<?> vc)
    {
        super(vc);
    }

    @Override
    public Hoot deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException
    {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode     tree   = mapper.readTree(jsonParser);

        switch (HootType.valueOf(tree.get("hootType").asText())) {
            case Post:
                return mapper.treeToValue(tree, Post.class);
            case Comment:
                return mapper.treeToValue(tree, Comment.class);
            case Image:
                return mapper.treeToValue(tree, Image.class);
        }

        throw new JsonParseException(jsonParser, "No matching hoot type found.");
    }
}
