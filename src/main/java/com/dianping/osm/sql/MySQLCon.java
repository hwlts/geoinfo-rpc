package com.dianping.osm.sql;


import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLCon
{
    private String url = "jdbc:mysql://192.168.8.44:3306/DianPingMap";
    private String username = "dpcom_map";
    private String password = "dp!@tempuser";

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
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
