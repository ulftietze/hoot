package hoot.system.ObjectManager;

import java.util.HashMap;
import java.util.Map;

public class ObjectManager
{
    private static ObjectManager instance;

    private final Map<Class<?>, Object> objectMap;

    private Map<Class<?>, FactoryInterface<?>> objectFactoryMap;

    private ObjectManager()
    {
        this.objectMap = new HashMap<>();
    }

    public static Object get(Class<?> className)
    {
        ObjectManager om = ObjectManager.getInstance();
        return om.objectMap.get(className);
    }

    public static Object get(Class<?> className, boolean newInstance)
    {
        ObjectManager om = ObjectManager.getInstance();

        if (newInstance) {
            FactoryInterface<?> factory = om.objectFactoryMap.get(className);
            return factory.create();
        }

        return ObjectManager.get(className);
    }

    public static void set(Class<?> className, Object object)
    {
        ObjectManager om = ObjectManager.getInstance();
        om.objectMap.put(className, object);
    }

    public static void set(Class<?> className, Object object, FactoryInterface<?> factory)
    {
        ObjectManager om = ObjectManager.getInstance();
        om.objectMap.put(className, object);
        om.objectFactoryMap.put(className, factory);
    }

    private static ObjectManager getInstance()
    {
        if (instance == null) {
            instance = new ObjectManager();
        }

        return instance;
    }
}
