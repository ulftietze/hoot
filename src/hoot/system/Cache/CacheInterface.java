package hoot.system.Cache;

public interface CacheInterface<Type>
{
    Type get(Integer id);

    void put(Type entry);

    void purge(Type entry);

    Integer getSize();
}
