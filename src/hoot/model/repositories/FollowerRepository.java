package hoot.model.repositories;

import hoot.model.entities.Follower;
import hoot.model.entities.User;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

import java.util.ArrayList;

public class FollowerRepository extends AbstractRepository<Follower>
{
    public int getFollowerCountForUser(User user)
    {
        return 0;
    }

    @Override
    public ArrayList<Follower> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        // limit to 50 with FollowerToUser SearchCriteria
        return new ArrayList<>();
    }

    @Override
    public void save(Follower entity) throws CouldNotSaveException
    {

    }

    @Override
    public void delete(Follower entity) throws CouldNotDeleteException
    {

    }
}
