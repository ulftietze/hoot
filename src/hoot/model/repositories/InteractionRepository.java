package hoot.model.repositories;

import hoot.model.entities.HootType;
import hoot.model.entities.Interaction;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class InteractionRepository extends AbstractRepository<Interaction>
{
    @Override
    public Interaction getById(int id) throws EntityNotFoundException
    {
        return null;
    }

    /**
     * Get a list of all Interactions in the database.
     * @param searchCriteria is silently ignored
     * @return A list of all Interactions
     * @throws EntityNotFoundException If no Interaction is found or the connection failed
     */
    @Override
    public ArrayList<Interaction> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        ArrayList<Interaction> resultSet = new ArrayList<>();

        try {
            Connection connection = this.getConnection();

            String            sqlStatement = "select * from Interaction";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);
            ResultSet         rs           = pss.executeQuery();

            rs.next();  // will throw SQLException if DB contains no interaction

            do {
                Interaction type = new Interaction();
                type.interaction = rs.getString("interaction");
                resultSet.add(type);
            } while (rs.next());

            return resultSet;
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new EntityNotFoundException("HootType");
        }
    }

    @Override
    public Interaction create() throws CouldNotSaveException
    {
        return null;
    }

    @Override
    public void save(Interaction interaction) throws CouldNotSaveException
    {

    }

    @Override
    public void delete(Interaction interaction) throws CouldNotDeleteException
    {

    }
}
