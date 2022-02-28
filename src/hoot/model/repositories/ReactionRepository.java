package hoot.model.repositories;

import hoot.model.entities.Interaction;
import hoot.model.entities.Reaction;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.QueryBuilder;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReactionRepository extends AbstractRepository<Reaction>
{
    @Override
    public ArrayList<Reaction> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        ArrayList<Reaction> reactions = new ArrayList<>();

        try {
            Connection   connection   = this.getConnection();
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();
            queryBuilder.SELECT.add("*");
            queryBuilder.FROM = "Reaction r";

            PreparedStatement statement = queryBuilder.build(connection);
            ResultSet         resultSet = statement.executeQuery();

            while (resultSet.next()) {
                reactions.add(this.mapResultSet(resultSet));
            }
        } catch (SQLException e) {
            this.log("Something went wrong while loading reactions list: " + e.getMessage());
            throw new EntityNotFoundException("Reaction");
        }

        return reactions;
    }

    @Override
    public void save(Reaction reaction) throws CouldNotSaveException
    {
        try {
            Connection connection = this.getConnection();
            String query = "INSERT INTO Reaction (user, hoot, interaction) " + "VALUES (?,?,?) "
                           + "ON DUPLICATE KEY UPDATE " + "interaction = VALUES(interaction)";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, reaction.user.id);
            statement.setInt(2, reaction.hoot.id);
            statement.setString(3, reaction.interaction.toString());

            statement.executeQuery().close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            this.log("Something went wrong while loading reactions list: " + e.getMessage());
            throw new CouldNotSaveException("Reaction");
        }
    }

    @Override
    public void delete(Reaction reaction) throws CouldNotDeleteException
    {
        try {
            Connection connection = this.getConnection();
            String     query      = "DELETE FROM Reaction WHERE user = ? AND hoot = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, reaction.user.id);
            statement.setInt(2, reaction.hoot.id);

            statement.executeQuery().close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            this.log("Something went wrong while deleting reaction: " + e.getMessage());
            throw new CouldNotDeleteException("Reaction");
        }
    }

    private Reaction mapResultSet(ResultSet resultSet) throws SQLException
    {
        UserRepository userRepository = (UserRepository) ObjectManager.get(UserRepository.class);
        HootRepository hootRepository = (HootRepository) ObjectManager.get(HootRepository.class);

        Reaction reaction = (Reaction) ObjectManager.create(Reaction.class);

        reaction.interaction = Interaction.valueOf(resultSet.getString("interaction"));
        reaction.user        = userRepository.getById(resultSet.getInt("user"));
        reaction.hoot        = hootRepository.getById(resultSet.getInt("hoot"));

        return reaction;
    }
}
