package hoot.initialisation.Factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.json.JsonMapper.Builder;
import hoot.system.ObjectManager.FactoryInterface;

public class JacksonSerializerFactory implements FactoryInterface<ObjectMapper>
{
    @Override
    public ObjectMapper create()
    {
        Builder builder = JsonMapper.builder();
        builder.findAndAddModules();
        builder.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ObjectMapper mapper = builder.build();
        mapper.setDefaultPrettyPrinter(mapper.getSerializationConfig().constructDefaultPrettyPrinter());

        return mapper;
    }
}
