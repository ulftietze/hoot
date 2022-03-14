package hoot.model.cache;

import hoot.model.entities.Hoot;

public class HootEmptyCache implements HootCacheInterface
{
    @Override
    public Hoot get(Integer id)
    {
        return null;
    }

    @Override
    public void put(Hoot entry)
    {

    }

    @Override
    public void purge(Hoot entry)
    {

    }

    @Override
    public void clean()
    {

    }

    @Override
    public Integer getSize()
    {
        return 0;
    }
}
