package hoot.model.repositories;

import hoot.model.entities.Interaction;
import hoot.model.entities.Reaction;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.QueryBuilder;
import hoot.system.Database.QueryResult;
import hoot.system.Database.QueryResultRow;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.objects.Inject;
import hoot.system.objects.ObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReactionRepository extends AbstractRepository<Reaction>
{
    @Inject private UserRepository userRepository;
    @Inject private HootRepository hootRepository;

    @Override
    public ArrayList<Reaction> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        ArrayList<Reaction> reactions = new ArrayList<>();

        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();
            queryBuilder.SELECT.add("*");
            queryBuilder.FROM = "Reaction r";

            PreparedStatement statement   = queryBuilder.build(connection);
            QueryResult       queryResult = this.statementFetcher.fetchAll(statement);
            connection.close();

            for (QueryResultRow row : queryResult) {
                reactions.add(this.mapResultSet(row));
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
        try (Connection connection = this.getConnection()) {
            String query = "INSERT INTO Reaction (user, hoot, interaction) " + "VALUES (?,?,?) "
                           + "ON DUPLICATE KEY UPDATE " + "interaction = VALUES(interaction)";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, reaction.user.id);
            statement.setInt(2, reaction.hoot.id);
            statement.setString(3, reaction.interaction.toString());

            this.statementFetcher.executeUpdate(statement);
        } catch (SQLException e) {
            this.log("Something went wrong while loading reactions list: " + e.getMessage());
            throw new CouldNotSaveException("Reaction");
        }
    }

    @Override
    public void delete(Reaction reaction) throws CouldNotDeleteException
    {
        try (Connection connection = this.getConnection()) {
            String query = "DELETE FROM Reaction WHERE user = ? AND hoot = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, reaction.user.id);
            statement.setInt(2, reaction.hoot.id);

            this.statementFetcher.executeUpdate(statement);
        } catch (SQLException e) {
            this.log("Something went wrong while deleting reaction: " + e.getMessage());
            throw new CouldNotDeleteException("Reaction");
        }
    }

    private Reaction mapResultSet(QueryResultRow resultRow) throws SQLException
    {
        Reaction reaction = (Reaction) ObjectManager.create(Reaction.class);

        reaction.interaction = Interaction.valueOf((String) resultRow.get("Reaction.interaction"));
        reaction.user        = this.userRepository.getById((int) resultRow.get("Reaction.user"));
        reaction.hoot        = this.hootRepository.getById((int) resultRow.get("Reaction.hoot"));

        return reaction;
    }
}
