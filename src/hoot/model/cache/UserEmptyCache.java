package hoot.model.cache;

import hoot.model.entities.User;

public class UserEmptyCache implements UserCacheInterface
{
    @Override
    public User get(Integer id)
    {
        return null;
    }

    @Override
    public User get(String username)
    {
        return null;
    }

    @Override
    public void put(User entry)
    {

    }

    @Override
    public void purge(User entry)
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
