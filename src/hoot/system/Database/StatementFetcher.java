package hoot.system.Database;

import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class StatementFetcher
{
    public QueryResultRow fetchOne(PreparedStatement statement) throws SQLException
    {
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        QueryResultRow row = this.mapRow(resultSet);

        resultSet.close();
        statement.close();

        return row;
    }

    public QueryResult fetchAll(PreparedStatement statement) throws SQLException
    {
        LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);

        Instant start = Instant.now();

        var       fetchData = (QueryResult) ObjectManager.create(QueryResult.class);
        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery();

            Duration executionTime = Duration.between(start, Instant.now());
            if (executionTime.toMillis() > 1000) {
                logger.log("Slow Query Detected Duration: " + executionTime.toMillis());
            }

            while (resultSet.next()) {
                fetchData.add(this.mapRow(resultSet));
            }
        } catch (Throwable t) {
            throw t;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            statement.close();
        }

        return fetchData;
    }

    public int executeUpdate(PreparedStatement statement) throws SQLException
    {
        int rowCount = statement.executeUpdate();
        statement.close();

        return rowCount;
    }

    private QueryResultRow mapRow(ResultSet resultSet) throws SQLException
    {
        ResultSetMetaData metaData = resultSet.getMetaData();
        QueryResultRow    row      = (QueryResultRow) ObjectManager.create(QueryResultRow.class);

        for (int i = 1; i <= metaData.getColumnCount(); ++i) {
            String column = "";

            if (!metaData.getTableName(i).equals("")) {
                column = metaData.getTableName(i) + "." + metaData.getColumnLabel(i);
            } else {
                column = metaData.getColumnLabel(i);
            }

            //this.debugResultSet(resultSet, i, column);

            //if (row.containsKey(column)) {
            //    throw new SQLException("Column " + column + " is represented at least twice.");
            //}

            switch (resultSet.getMetaData().getColumnType(i)) {
                case Types.BIGINT:
                    // TODO: This is legit no joke lol.
                    // Since the IDs are an unsigned int, it's a long and int at the same time
                    // Since we don't want to modify the whole code base, this is the easiest workaround
                    if (Objects.equals("INTEGER", resultSet.getMetaData().getColumnTypeName(i))) {
                        row.put(column, resultSet.getInt(i));
                    } else {
                        row.put(column, resultSet.getLong(i));
                    }
                    break;
                case Types.INTEGER:
                case Types.SMALLINT:
                    row.put(column, resultSet.getInt(i));
                    break;
                case Types.BIT:
                case Types.TINYINT:
                case Types.BOOLEAN:
                    row.put(column, resultSet.getBoolean(i));
                    break;
                case Types.ARRAY:
                    row.put(column, resultSet.getArray(i));
                    break;
                case Types.TIME:
                    row.put(column, resultSet.getTime(i));
                    break;
                case Types.TIMESTAMP:
                    row.put(column, resultSet.getTimestamp(i));
                    break;
                case Types.VARCHAR:
                    row.put(column, resultSet.getString(i));
                    break;
                case Types.DOUBLE:
                    row.put(column, resultSet.getDouble(i));
                    break;
                case Types.FLOAT:
                    row.put(column, resultSet.getFloat(i));
                    break;
                case Types.DECIMAL:
                    row.put(column, resultSet.getBigDecimal(i));
                    break;
                default:
                    row.put(column, resultSet.getObject(i));
                    break;
            }
        }

        return row;
    }

    private void debugResultSet(ResultSet resultSet, int i, String column) throws SQLException
    {
        if (!column.contains("id")) {
            return;
        }

        LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);

        int    type     = resultSet.getMetaData().getColumnType(i);
        String typeName = resultSet.getMetaData().getColumnTypeName(i);
        logger.log("Entry (" + column + " => " + resultSet.getObject(i) + ")::" + typeName + "::" + type);
    }
}
