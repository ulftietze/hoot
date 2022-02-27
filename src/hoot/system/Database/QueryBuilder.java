package hoot.system.Database;

import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class QueryBuilder
{
    public ArrayList<String> SELECT     = new ArrayList<>();
    public String            FROM       = "";
    public ArrayList<String> JOINS      = new ArrayList<>();
    public ArrayList<String> WHERE      = new ArrayList<>();
    public ArrayList<String> GROUP_BY   = new ArrayList<>();
    public ArrayList<String> ORDER_BY   = new ArrayList<>();
    public Integer           LIMIT      = 50;
    public ArrayList<Object> PARAMETERS = new ArrayList<>();

    public PreparedStatement build(Connection connection) throws SQLException
    {
        StringBuilder QUERY = new StringBuilder();

        this.addSelect(QUERY);
        this.addFrom(QUERY);
        this.addJoin(QUERY);
        this.addWhere(QUERY);
        this.addGroupBy(QUERY);
        this.addOrderBy(QUERY);
        this.addLimit(QUERY);

        PreparedStatement statement = connection.prepareStatement(QUERY.toString());
        this.mapParameters(statement);

        LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
        logger.log(QUERY.toString() + " [parameters=" + this.PARAMETERS.toString() + "]");

        return statement;
    }

    private void mapParameters(PreparedStatement statement) throws SQLException
    {
        for (int i = 1; i <= this.PARAMETERS.size(); i++) {
            Object param = this.PARAMETERS.get(i - 1);
            LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
            logger.log(param.getClass().getName());

            if (param instanceof LocalDateTime) {
                statement.setTimestamp(i, this.getSQLTimestampFromLocalDateTime((LocalDateTime) param));
            } else if (param instanceof Integer) {
                statement.setInt(i, (Integer) param);
            } else if (param instanceof Long) {
                statement.setLong(i, (Long) param);
            } else if (param instanceof Double) {
                statement.setDouble(i, (Double) param);
            } else if (param instanceof Float) {
                statement.setFloat(i, (Float) param);
            } else if (param instanceof Object[]) {
                statement.setArray(i, statement.getConnection().createArrayOf("text", (Object[]) param));
            } else if (param instanceof String){
                statement.setString(i, (String) param);
            } else {
                statement.setObject(i, param);
            }
        }
    }

    private Timestamp getSQLTimestampFromLocalDateTime(LocalDateTime dateTime)
    {
        return Timestamp.from(dateTime.toInstant(ZoneId.of("Europe/Berlin").getRules().getOffset(dateTime)));
    }

    private void addJoin(StringBuilder query)
    {
        for (String join : this.JOINS) {
            query.append(" ").append(join);
        }
    }

    private void addSelect(StringBuilder query)
    {
        if (this.SELECT.size() >= 1) {
            query.append("SELECT ").append(this.SELECT.get(0));

            for (int i = 1; i < this.SELECT.size(); i++) {
                query.append(", ").append(this.SELECT.get(i));
            }
        } else {
            query.append("SELECT * ");
        }
    }

    private void addFrom(StringBuilder query)
    {
        query.append(" FROM ").append(this.FROM).append(" ");
    }

    private void addWhere(StringBuilder query)
    {
        if (this.WHERE.size() >= 1) {
            query.append(" WHERE ").append(this.WHERE.get(0));

            for (int i = 1; i < this.WHERE.size(); i++) {
                query.append(" AND ").append(this.WHERE.get(i));
            }
        }
    }

    private void addGroupBy(StringBuilder query)
    {
        if (this.GROUP_BY.size() >= 1) {
            query.append(" GROUP BY ").append(this.GROUP_BY.get(0));

            for (int i = 1; i < this.GROUP_BY.size(); i++) {
                query.append(", ").append(this.GROUP_BY.get(i));
            }
        }
    }

    private void addOrderBy(StringBuilder query)
    {
        if (this.ORDER_BY.size() >= 1) {
            query.append(" ORDER BY ").append(this.ORDER_BY.get(0));

            for (int i = 1; i < this.ORDER_BY.size(); i++) {
                query.append(", ").append(this.ORDER_BY.get(i));
            }
        }
    }

    private void addLimit(StringBuilder query)
    {
        query.append(" LIMIT ").append(LIMIT);
    }
}
