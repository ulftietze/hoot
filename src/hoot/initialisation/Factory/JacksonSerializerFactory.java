package hoot.initialisation.Factory;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import hoot.system.ObjectManager.FactoryInterface;
import hoot.system.ObjectManager.ObjectManager;

public class JacksonSerializerFactory implements FactoryInterface<ObjectMapper>
{
    @Override
    public ObjectMapper create()
    {
        ObjectMapper mapper = new JsonMapper();
        mapper.findAndRegisterModules();
        mapper.setDefaultPrettyPrinter((DefaultPrettyPrinter) ObjectManager.get(DefaultPrettyPrinter.class));

        return mapper;
    }
}
