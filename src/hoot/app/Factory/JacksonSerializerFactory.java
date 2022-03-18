package hoot.app.Factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.json.JsonMapper.Builder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import hoot.model.entities.Hoot;
import hoot.model.serializer.HootDeserializer;
import hoot.system.objects.FactoryInterface;

public class JacksonSerializerFactory implements FactoryInterface<ObjectMapper>
{
    @Override
    public ObjectMapper create()
    {
        Builder builder = JsonMapper.builder();
        builder.findAndAddModules();
        builder.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Hoot.class, new HootDeserializer());

        ObjectMapper mapper = builder.build();
        mapper.registerModule(module);
        mapper.setDefaultPrettyPrinter(mapper.getSerializationConfig().constructDefaultPrettyPrinter());

        return mapper;
    }
}
