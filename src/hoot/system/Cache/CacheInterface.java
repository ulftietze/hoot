package hoot.system.Cache;

public interface CacheInterface<Type>
{
    String getIdentifier();

    Type get(Integer id);

    void put(Type entry);

    void purge(Type entry);

    void clean();

    Integer getSize();
}
