package hoot.model.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import hoot.model.entities.*;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Serializer.Serializer;

import java.io.IOException;

public class HootDeserializer extends StdDeserializer<Hoot>
{
    public HootDeserializer()
    {
        this(null);
    }

    public HootDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Hoot deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException
    {
        Serializer serializer = (Serializer) ObjectManager.get(Serializer.class);
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        //LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
        //logger.log("LOG: " + node.toPrettyString());

        switch (HootType.valueOf(node.get("hootType").asText())) {
            case Post:
                return (Hoot) serializer.deserialize(node.toString(), Post.class);
            case Comment:
                return (Hoot) serializer.deserialize(node.toString(), Comment.class);
            case Image:
                return (Hoot) serializer.deserialize(node.toString(), Image.class);
        }

        throw new JsonParseException(jsonParser, "No matching hoot type found.");
    }
}
