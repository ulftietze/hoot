package hoot.system.ObjectManager;

import java.util.HashMap;
import java.util.Map;

public class ObjectManager
{
    private static ObjectManager instance;

    private Map<Class<?>, Object> objectMap;

    private ObjectManager()
    {
        this.objectMap = new HashMap<>();
    }

    public static Object get(Class<?> className)
    {
        ObjectManager om = ObjectManager.getInstance();
        return om.objectMap.get(className);
    }

    public static void set(Class<?> className, Object object)
    {
        ObjectManager om = ObjectManager.getInstance();
        om.objectMap.put(className, object);
    }

    private static ObjectManager getInstance()
    {
        if (instance == null) {
            instance = new ObjectManager();
        }

        return instance;
    }
}
