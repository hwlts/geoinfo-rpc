package com.dianping.osm.insertIntoGis;

import com.dianping.osm.sql.MySQLCon;
import com.dianping.osm.sql.PostgreSQLCon;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class addByTable
{
    private Map<String,String> CityNameToPinYin = new HashMap<String,String>();
    {
        CityNameToPinYin.put("上海","shanghai");
        CityNameToPinYin.put("","");
    }

    private Map<String,String> CityNameToIndex = new HashMap<String,String>();
    {
        CityNameToIndex.put("","");
    }

    public void addCity(String city)
            throws SQLException
    {
        PostgreSQLCon postgreSQLCon = new PostgreSQLCon();
        Connection postgresqlCon = postgreSQLCon.getConnection();
        Statement selectSt = postgresqlCon.createStatement();

        MySQLCon mySQLCon = new MySQLCon();
        Connection mysqlcon = mySQLCon.getConnection();
        if (mysqlcon != null)
        {
            String transverse = "SELECT * FROM ";
            Statement mysqlSt = mysqlcon.createStatement();
            ResultSet mysqlRs = mysqlSt.executeQuery(transverse);
            while (mysqlRs.next())
            {
                String s;
                String selectSqlStr = "SELECT * FROM "+"ST_Distance";
                ResultSet isNear = selectSt.executeQuery(selectSqlStr);
                if (!isNear.next())
                {
                    String insertSqlStr = "";
                    PreparedStatement insertSt = postgresqlCon.prepareStatement(insertSqlStr);
                    insertSt.execute();
                    insertSt.close();
                }
            }
        }
    }
}
