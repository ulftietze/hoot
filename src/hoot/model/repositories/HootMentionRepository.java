package hoot.model.repositories;

import hoot.model.entities.Mentions;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

import java.util.ArrayList;

public class HootMentionRepository extends AbstractRepository<Mentions>
{
    public Mentions getByHootId(Integer hootId)
    {
        return null;
    }

    @Override
    public ArrayList<Mentions> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        return null;
    }

    @Override
    public void save(Mentions mentions) throws CouldNotSaveException
    {

    }

    @Override
    public void delete(Mentions mentions) throws CouldNotDeleteException
    {

    }
}
