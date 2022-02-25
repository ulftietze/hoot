package hoot.model.repositories;

import hoot.model.entities.Hoot;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HootRepository extends AbstractRepository<Hoot>
{
    public Hoot getById(int id)
    {
        return null;
    }

    @Override
    public ArrayList<Hoot> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        ArrayList<Hoot> hoots = new ArrayList<>();

        try {
            Connection        connection = this.getConnection();
            PreparedStatement statement  = searchCriteria.getQueryStatement(connection);
            ResultSet         resultSet  = statement.executeQuery();

            while (resultSet.next()) {
                // TODO: This must be based on the HootType
                Hoot hoot = new Hoot();

                // TODO: Full Mapping
                hoot.id = resultSet.getInt("id");
                hoots.add(hoot);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            // TODO: Maybe logging and stuff, maybe not, this could
            this.log(e.getMessage());
        }

        return hoots;
    }

    @Override
    public void save(Hoot hootType) throws CouldNotSaveException
    {

    }

    @Override
    public void delete(Hoot hootType) throws CouldNotDeleteException
    {

    }
}
