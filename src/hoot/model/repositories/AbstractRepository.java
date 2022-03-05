package hoot.model.repositories;

import hoot.model.search.DefaultSearchCriteria;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public abstract class AbstractRepository<Type>
{
    public ArrayList<Type> getList() throws EntityNotFoundException
    {
        return this.getList((DefaultSearchCriteria) ObjectManager.get(DefaultSearchCriteria.class));
    }

    public abstract ArrayList<Type> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException;

    public abstract void save(Type type) throws CouldNotSaveException;

    public abstract void delete(Type type) throws CouldNotDeleteException;

    /**
     * TODO: Do we need synchronized here?
     *
     * @return
     * @throws SQLException
     */
    protected Connection getConnection() throws SQLException
    {
        DataSource ds = (DataSource) ObjectManager.get(DataSource.class);
        return ds.getConnection();
    }

    /**
     * TODO: Do we need synchronized here?
     *
     * @param message
     */
    protected void log(String message)
    {
        LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
        logger.log(message);
    }

    protected LocalDateTime getLocalDateTimeFromSQLTimestamp(Timestamp timestamp)
    {
        return timestamp.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDateTime();
    }

    protected Timestamp getSQLTimestampFromLocalDateTime(LocalDateTime dateTime)
    {
        return Timestamp.from(dateTime.toInstant(ZoneId.of("Europe/Berlin").getRules().getOffset(dateTime)));
    }
}
