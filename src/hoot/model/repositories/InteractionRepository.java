package hoot.model.repositories;

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
    public Interaction getById(int id)
    {
        return null;
    }

    /**
     * Get a list of all Interactions in the database.
     *
     * @param searchCriteria is silently ignored
     * @return A list of all Interactions
     * @throws EntityNotFoundException If no Interaction is found or the connection failed
     */
    @Override
    public ArrayList<Interaction> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        ArrayList<Interaction> interactionList = new ArrayList<>();

        try {
            Connection connection = this.getConnection();

            String            sqlStatement = "select * from Interaction";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);
            ResultSet         rs           = pss.executeQuery();

            rs.next();  // will throw SQLException if DB contains no interaction

            do {
                Interaction type = new Interaction();
                type.interaction = rs.getString("interaction");
                interactionList.add(type);
            } while (rs.next());

            return interactionList;
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new EntityNotFoundException("HootType");
        }
    }

    /**
     * Save an Interaction object to DB.
     *
     * @param interaction any interaction object
     * @throws CouldNotSaveException if an SQL error occurred
     */
    @Override
    public void save(Interaction interaction) throws CouldNotSaveException
    {
        try {
            Connection connection = this.getConnection();

            String            sqlStatement = "insert into Interaction (interaction) VALUES (?)";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);
            pss.setString(1, interaction.interaction);
            int rowCount = pss.executeUpdate();

            if (rowCount == 0) {
                throw new CouldNotSaveException("Interaction of type " + interaction.interaction);
            }

            pss.close();
            connection.close();
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotSaveException("Interaction of type " + interaction.interaction);
        }
    }

    /**
     * Delte Interaction object from DB.
     * @param interaction any interaction object
     * @throws CouldNotDeleteException if an SQL error occurred
     */
    @Override
    public void delete(Interaction interaction) throws CouldNotDeleteException
    {
        try {
            Connection connection = this.getConnection();

            String            sqlStatement = "delete from Interaction where interaction = ?";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);
            pss.setString(1, interaction.interaction);
            int rowCount = pss.executeUpdate();

            if (rowCount == 0) {
                throw new CouldNotDeleteException("Interaction of type " + interaction.interaction);
            }

            pss.close();
            connection.close();
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotDeleteException("Interaction of type " + interaction.interaction);
        }
    }
}
