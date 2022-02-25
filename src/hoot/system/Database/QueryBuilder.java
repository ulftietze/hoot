package hoot.system.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    public ArrayList<String> PARAMETERS = new ArrayList<>();

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

        for (int i = 1; i <= PARAMETERS.size(); i++) {
            statement.setString(i, PARAMETERS.get(i - 1));
        }

        return statement;
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
