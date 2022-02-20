package hoot.model.repositories;

import hoot.front.api.dto.user.UserDTO;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserRepository implements RepositoryInterface<UserDTO>
{
    public UserDTO getById(int id) throws EntityNotFoundException
    {
        try {
            Context    initCtx    = new InitialContext();
            Context    envCtx     = (Context) initCtx.lookup("java:/comp/env");
            DataSource ds         = (DataSource) envCtx.lookup("jdbc/mariadb");
            Connection connection = ds.getConnection();

            PreparedStatement pss = connection.prepareStatement("select * from User where id = ?");
            pss.setInt(1, id);
            ResultSet rs = pss.executeQuery();

            rs.next(); // will throw exception if user not found
            if (!rs.isLast()) {
                throw new EntityNotFoundException("User");
            }

            UserDTO user = new UserDTO();

            user.id       = rs.getInt("id");
            user.username = rs.getString("username");
            user.imageUrl = rs.getString("imagePath");

            rs.close();
            pss.close();
            connection.close();

            return user;
        } catch (Exception e) {
            throw new EntityNotFoundException("User");
        }
    }

    public void save(UserDTO user) throws CouldNotSaveException
    {

    }

    public void delete(UserDTO user) throws CouldNotDeleteException
    {

    }
}
