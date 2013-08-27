package com.dianping.osm.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnector
{
    private String diverName = "org.postgresql.Driver";
    private String url = "jdbc:postgresql://localhost:5432/chinaboundaries";
    private String user = "postgres";
    private String password = "admin";

    public void setDiverName(String newDriverName)
    {
        this.diverName = newDriverName;
    }

    public String getDiverName()
    {
        return this.diverName;
    }

    public void setUrl(String newurl)
    {
        this.url = newurl;
    }

    public String getUrl()
    {
        return this.url;
    }

    public void setUser(String newUser)
    {
        this.user = newUser;
    }

    public String getUser()
    {
        return this.user;
    }

    public void setPassword(String newPassword)
    {
        this.password = newPassword;
    }

    public String getPassord()
    {
        return this.password;
    }

    public Connection getConnection()
    {
        try
        {
            Class.forName(diverName);
            return DriverManager.getConnection(url,user,password);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args)
    {
        DBConnector connector = new DBConnector();
        Connection con = connector.getConnection();
        try
        {
            Statement st = con.createStatement();
            String sql = "select * from nodes where id=25926073";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next())
            {
                System.out.println(rs.getString(5));
            }
            rs.close();
            st.close();
            con.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
