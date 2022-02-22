package hoot.model.entities;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class User implements SQLData
{
    private String sqlType;

    public int id;

    public String username;
    public String imagePath;
    public String passwordHash;

    public Timestamp     lastLogin;
    public LocalDateTime created;
    public LocalDateTime modified;

    @Override
    public String getSQLTypeName() throws SQLException
    {
        return sqlType;
    }

    @Override
    public void readSQL(SQLInput stream, String type) throws SQLException
    {
        sqlType = type;
        this.id = stream.readInt();
        this.username = stream.readString();
        this.imagePath = stream.readString();
        this.passwordHash = stream.readString();
        this.lastLogin = stream.readTimestamp();

        LocalDateTime test = lastLogin.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDateTime();
    }

    @Override
    public void writeSQL(SQLOutput sqlOutput) throws SQLException
    {

    }
}
