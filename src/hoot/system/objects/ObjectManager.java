package hoot.system.objects;

import hoot.system.Logger.LoggerInterface;
import hoot.system.Logger.NullLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ObjectManager
{
    private static ObjectManager instance;

    private final Map<Class<?>, Class<?>> classMap = new HashMap<>();

    private final Map<Class<?>, Object> objectMap = new HashMap<>();

    private final Map<Class<?>, FactoryInterface<?>> objectFactoryMap = new HashMap<>();

    private ObjectManager() {}

    /**
     * Get an instance of an object for the given class name.
     * When the class does not exist, try to create this. This is kind of a lazy loading the class.
     *
     * @param className of the Object to create.
     * @return an instance of the given className. This may be a child class.
     */
    public static Object get(Class<?> className, Object... arguments)
    {
        ObjectManager om          = ObjectManager.getInstance();
        Class<?>      actualClass = om.getActualClassToCreate(className);

        if (om.objectMap.get(className) == null) {
            om.objectMap.put(className, om.create(className, actualClass, arguments));
        }

        return om.objectMap.get(className);
    }

    /**
     * Create an instance of an object for the given class name.
     *
     * @param className of the Object to create.
     * @return an instance of the given className. This may be a child class.
     */
    public static Object create(Class<?> className, Object... arguments)
    {
        ObjectManager om          = ObjectManager.getInstance();
        Class<?>      actualClass = om.getActualClassToCreate(className);

        return om.create(className, actualClass, arguments);
    }

    /**
     * Add a ClassMapping.
     * This is especially necessary for Interfaces or AbstractClasses. This enables to change the concrete
     * implementation without changing anything inside the business logic.
     * <p>
     * Example:
     * LoggerInterface.class => ContextLogger.class
     * or
     * LoggerInterface.class => NullLogger.class
     *
     * @param className   which identifies as key
     * @param actualClass the class name which should be loaded when className is called.
     */
    public static void set(Class<?> className, Class<?> actualClass)
    {
        ObjectManager om = ObjectManager.getInstance();
        om.classMap.put(className, actualClass);
    }

    /**
     * Add a ClassObjectMapping.
     * This enables the direct mapping of an object without lazy loading. This puts the object directly to the
     * ClassObjectMapping.
     * <p>
     * This is especially necessary for Interfaces or AbstractClasses. This enables to change the concrete
     * implementation without changing anything inside the business logic.
     * <p>
     * Example:
     * LoggerInterface.class => ContextLogger.class
     * or
     * LoggerInterface.class => NullLogger.class
     *
     * @param className which identifies as key
     * @param object    the concrete object which should be loaded when className is called.
     */
    public static void set(Class<?> className, Object object)
    {
        ObjectManager om = ObjectManager.getInstance();
        om.objectMap.put(className, object);
    }

    /**
     * Add a ClassName on FactoryMapping.
     * This enables the lazy loading on objects but with more complex logic. If a class has a more complex
     * logic to get created, we can add a factory which will be called on class loading.
     *
     * @param className which identifies as key
     * @param factory   the factory which should be able to create an instance of the given class name.
     */
    public static void setFactory(Class<?> className, FactoryInterface<?> factory)
    {
        ObjectManager om = ObjectManager.getInstance();
        om.objectFactoryMap.put(className, factory);
    }

    private static ObjectManager getInstance()
    {
        if (instance == null) {
            instance = new ObjectManager();
        }

        return instance;
    }

    private Class<?> getActualClassToCreate(Class<?> className)
    {
        Class<?> actualClass = this.classMap.get(className);

        return actualClass != null ? actualClass : className;
    }

    private Object create(Class<?> className, Class<?> actualClass, Object... arguments)
    {
        FactoryInterface<?> factory = this.getFactoryIfExists(className, actualClass);

        if (factory != null) {
            return factory.create();
        }

        try {
            if (!this.canCreateInstance(actualClass)) {
                throw new InstantiationException("Can't create instance.");
            }

            Object instance;

            if (actualClass.getEnclosingClass() != null) {
                Class<?>[] argumentTypes = new Class[arguments.length];
                argumentTypes[0] = actualClass.getEnclosingClass();

                for (int i = 1; i <= arguments.length; i++) {
                    argumentTypes[i] = arguments[i].getClass();
                }

                Object[] argumentObjects = new Object[arguments.length + 1];
                argumentObjects[0] = ObjectManager.get(actualClass.getEnclosingClass());

                System.arraycopy(arguments, 0, argumentObjects, 1, arguments.length);

                Constructor<?> constructor = actualClass.getConstructor(argumentTypes);
                constructor.setAccessible(true);
                instance = constructor.newInstance(argumentObjects);
            } else {
                Class<?>[] argumentTypes = new Class[arguments.length];

                for (int i = 0; i < arguments.length; i++) {
                    argumentTypes[i] = arguments[i].getClass();
                }

                Constructor<?> constructor = actualClass.getConstructor(argumentTypes);
                constructor.setAccessible(true);
                instance = constructor.newInstance(arguments);
            }

            this.injectFields(instance);

            return instance;
        } catch (ReflectiveOperationException e) {
            String message = "[ERROR] Could not instantiate class " + className.getName() + ": " + e.getMessage();
            this.getLogger().logException(message, e);
            throw new RuntimeException(message);
        }
    }

    private void injectFields(Object instance) throws IllegalAccessException
    {
        ArrayList<Field> declaredFields = new ArrayList<>(Arrays.asList(instance.getClass().getDeclaredFields()));
        Class<?>         currentCass    = instance.getClass();

        while (currentCass.getSuperclass() != null) {
            currentCass = currentCass.getSuperclass();

            declaredFields.addAll(Arrays.asList(currentCass.getDeclaredFields()));
        }

        for (Field field : declaredFields) {
            if (field.getAnnotation(Inject.class) == null) {
                continue;
            }

            Class<?> concreteClass = field.getType();

            if (field.getAnnotation(Inject.class).concrete() != Object.class) {
                concreteClass = field.getAnnotation(Inject.class).concrete();
            }

            field.setAccessible(true);
            field.set(instance, field.getAnnotation(Inject.class).create() ? create(concreteClass) : get(concreteClass));
        }
    }

    private boolean canCreateInstance(Class<?> toCheck)
    {
        return !Modifier.isAbstract(toCheck.getModifiers());
    }

    private FactoryInterface<?> getFactoryIfExists(Class<?> className, Class<?> actualClass)
    {
        FactoryInterface<?> factory = this.objectFactoryMap.get(actualClass);

        if (factory == null) {
            factory = this.objectFactoryMap.get(className);
        }

        return factory;
    }

    private LoggerInterface getLogger()
    {
        LoggerInterface logger = (LoggerInterface) this.objectMap.get(LoggerInterface.class);

        return logger != null ? logger : new NullLogger();
    }
}