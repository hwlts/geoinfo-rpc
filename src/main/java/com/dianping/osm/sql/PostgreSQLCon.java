package com.dianping.osm.sql;


import java.sql.Connection;
import java.sql.DriverManager;

public class PostgreSQLCon
{
    private String url = "jdbc:postgresql://192.168.32.100:5432/test1";
    private String username = "postgres";
    private String password = "ws";

    public void setUrl(String u)
    {
        url = u;
    }

    public void setUsername(String n)
    {
        username = n;
    }

    public void setPassword(String p)
    {
        password = p;
    }

    public Connection getConnection()
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url,username,password);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
