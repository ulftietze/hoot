package hoot.system.Database;

import hoot.system.Logger.QueryLoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;

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

        this.buildSelect(QUERY);
        this.buildFrom(QUERY);
        this.buildJoin(QUERY);
        this.buildWhere(QUERY);
        this.buildGroupBy(QUERY);
        this.buildOrderBy(QUERY);
        this.buildLimit(QUERY);

        PreparedStatement statement = connection.prepareStatement(QUERY.toString());
        this.mapParameters(statement);

        QueryLoggerInterface logger = (QueryLoggerInterface) ObjectManager.get(QueryLoggerInterface.class);
        logger.log(QUERY.toString() + " [parameters=" + this.PARAMETERS.toString() + "]");

        return statement;
    }

    public void addWhere(String where, Object... params)
    {
        this.WHERE.add(where);
        this.PARAMETERS.addAll(Arrays.asList(params));
    }

    public void addJoin(String join)
    {
        this.JOINS.add(join);
    }

    public void addWhereIn(String where, ArrayList<?> inList)
    {
        if (inList.size() < 1) {
            return;
        }

        StringBuilder whereIn = new StringBuilder(where + " in (");

        for (int i = 0; i < inList.size(); i++) {
            whereIn.append("?");
            if (i < inList.size() - 1) {
                whereIn.append(", ");
            }
            this.PARAMETERS.add(inList.get(i));
        }

        whereIn.append(")");
        this.WHERE.add(whereIn.toString());
    }

    private void mapParameters(PreparedStatement statement) throws SQLException
    {
        for (int i = 1; i <= this.PARAMETERS.size(); i++) {
            Object param = this.PARAMETERS.get(i - 1);

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
            } else if (param instanceof String) {
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

    private void buildJoin(StringBuilder query)
    {
        for (String join : this.JOINS) {
            query.append(" ").append(join);
        }
    }

    private void buildSelect(StringBuilder query)
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

    private void buildFrom(StringBuilder query)
    {
        query.append(" FROM ").append(this.FROM).append(" ");
    }

    private void buildWhere(StringBuilder query)
    {
        if (this.WHERE.size() >= 1) {
            query.append(" WHERE ").append(this.WHERE.get(0));

            for (int i = 1; i < this.WHERE.size(); i++) {
                query.append(" AND ").append(this.WHERE.get(i));
            }
        }
    }

    private void buildGroupBy(StringBuilder query)
    {
        if (this.GROUP_BY.size() >= 1) {
            query.append(" GROUP BY ").append(this.GROUP_BY.get(0));

            for (int i = 1; i < this.GROUP_BY.size(); i++) {
                query.append(", ").append(this.GROUP_BY.get(i));
            }
        }
    }

    private void buildOrderBy(StringBuilder query)
    {
        if (this.ORDER_BY.size() >= 1) {
            query.append(" ORDER BY ").append(this.ORDER_BY.get(0));

            for (int i = 1; i < this.ORDER_BY.size(); i++) {
                query.append(", ").append(this.ORDER_BY.get(i));
            }
        }
    }

    private void buildLimit(StringBuilder query)
    {
        query.append(" LIMIT ").append(LIMIT);
    }
}
