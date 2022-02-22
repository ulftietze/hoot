package hoot.model.entities;

import java.io.Serializable;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class User implements Serializable
{
    public int id;

    public String username;
    public String imagePath;
    public String passwordHash;

    public LocalDateTime lastLogin;
    public LocalDateTime created;
    public LocalDateTime modified;
}
