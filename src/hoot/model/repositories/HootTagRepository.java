package hoot.model.repositories;

import hoot.model.entities.HootTags;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

import java.util.ArrayList;

public class HootTagRepository extends AbstractRepository<HootTags>
{
    public HootTags getByHootId(int id)
    {
        return null;
    }

    @Override
    public ArrayList<HootTags> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        return null;
    }

    @Override
    public void save(HootTags mentions) throws CouldNotSaveException
    {

    }

    @Override
    public void delete(HootTags mentions) throws CouldNotDeleteException
    {

    }
}
