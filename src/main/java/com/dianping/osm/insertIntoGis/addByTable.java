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
            String transverse = "SELECT * FROM CI_HotCity_10";
            Statement mysqlSt = mysqlcon.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
            mysqlSt.setFetchSize(Integer.MIN_VALUE);
            ResultSet mysqlRs = mysqlSt.executeQuery(transverse);
            long count = 0;
            while (mysqlRs.next())
            {
                ++count;
                if(count%10000 == 0)
                    System.out.println(count);
                String pointStr = mysqlRs.getString(2)+" "+mysqlRs.getString(1);
                String addressStr = '\''+mysqlRs.getString(3)+mysqlRs.getString(4)+mysqlRs.getString(5)+'\'';
                String selectSqlStr = "SELECT * FROM nodes_test WHERE ST_Distance(geom,ST_GeomFromText('POINT("+pointStr+")',4326)) < 50;";
                ResultSet isNear = selectSt.executeQuery(selectSqlStr);
                if (!isNear.next())
                {
                    String insertSqlStr = "INSERT INTO nodes_test(geom,address) values(ST_PointFromText('POINT("+pointStr+")', 4326),"+addressStr+")";
                    PreparedStatement insertSt = postgresqlCon.prepareStatement(insertSqlStr);
                    insertSt.execute();
                    insertSt.close();
                }
            }
        }
    }

    public static void main(String[] args)
    {
        addByTable a = new addByTable();
        try
        {
            a.addCity("");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
