package hoot.model.repositories;

import hoot.model.entities.HootType;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HootTypeRepository extends AbstractRepository<HootType>
{
    /**
     * HootTypes do not have an ID, thus always return null.
     * @return null
     */
    @Override
    public HootType getById(int id)
    {
        return null;
    }

    /**
     * Get a list of all HootTypes in the database.
     * @param searchCriteria is silently ignored
     * @return A list of all HootTypes. This list contains at least one Type, otherwise an Exception is thrown.
     * @throws EntityNotFoundException If no HootType is found or the connection failed
     */
    @Override
    public ArrayList<HootType> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        ArrayList<HootType> resultSet = new ArrayList<>();

        try {
            Connection connection = this.getConnection();

            String            sqlStatement = "select * from HootType";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);
            ResultSet         rs           = pss.executeQuery();

            rs.next();  // will throw SQLException if DB contains no HootTypes

            do {
                HootType type = new HootType();
                type.hootType = rs.getString("hootType");
                resultSet.add(type);
            } while (rs.next());

            return resultSet;
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new EntityNotFoundException("HootType");
        }
    }

    @Override
    public void save(HootType hootType) throws CouldNotSaveException
    {

    }

    @Override
    public void delete(HootType hootType) throws CouldNotDeleteException
    {

    }
}
