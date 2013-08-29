package com.dianping.osm.insertIntoGis;

import com.dianping.osm.sql.MySQLCon;
import com.dianping.osm.sql.PostgreSQLCon;

import java.sql.*;

public class addByTable
{
    private static class City
    {
        private String CityName;        //在PostgreSQL中的表名
        private String CityIndex;       //在MySQL中的表名
        private long CitySum;           //在MySQL中的行数

        public City(String n, String i, long s)
        {
            this.CityName = n;
            this.CityIndex = i;
            this.CitySum = s;
        }
    }

    private static City[] Cities = new City[15];
    static
    {
        Cities[0] = new City("shanghai","10",10206117);
        Cities[1] = new City("tianjin","100",2104667);
        Cities[2] = new City("ningbo","110",1004614);
        Cities[3] = new City("yangzhou","120",257684);
        Cities[4] = new City("wuxi","130",1166638);
        Cities[5] = new City("wuhan","160",1462519);
        Cities[6] = new City("xian","170",1413250);
        Cities[7] = new City("beijing","20",7446379);
        Cities[8] = new City("hangzhou","30",2143790);
        Cities[9] = new City("guangzhou","40",2631698);
        Cities[10] = new City("nanjing","50",1831760);
        Cities[11] = new City("suzhou","60",2361289);
        Cities[12] = new City("shenzhen","70",1975784);
        Cities[13] = new City("chengdu","80",1979365);
        Cities[14] = new City("chongqing","90",1219080);
    }

    public static void addCity(City c) throws SQLException
    {
        PostgreSQLCon postgreSQLCon = new PostgreSQLCon();
        Connection postgresqlCon = postgreSQLCon.getConnection();

        MySQLCon mySQLCon = new MySQLCon();
        Connection mysqlcon = mySQLCon.getConnection();
        if (mysqlcon != null)
        {
            String transverse = "SELECT * FROM CI_HotCity_"+c.CityIndex;
            Statement mysqlSt = mysqlcon.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            mysqlSt.setFetchSize(Integer.MIN_VALUE);
            ResultSet mysqlRs = mysqlSt.executeQuery(transverse);
            long count = 0;
            PreparedStatement insertSt = null;
            while (mysqlRs.next())
            {
                ++count;
                if (count % 100000 == 0)
                    System.out.println(((double)100*count / c.CitySum)+"%\t"+(new java.util.Date()));
                String lngStr = mysqlRs.getString(2);
                String latStr = mysqlRs.getString(1);
                if(!(lngStr.length()>=9 && latStr.length()>=8))
                    continue;
                String pointStr = lngStr.substring(0, 8) + " " + latStr.substring(0, 7);
                String street = mysqlRs.getString(5);
                if (street.contains("\'"))
                    continue;
                String addressStr = '\'' + mysqlRs.getString(3) + mysqlRs.getString(4) + mysqlRs.getString(5) + '\'';
                String insertSqlStr = "INSERT INTO nodes_"+c.CityName+"(geom,address) values(ST_PointFromText('POINT(" + pointStr + ")', 4326)," + addressStr + ")";
                insertSt = postgresqlCon.prepareStatement(insertSqlStr);
                insertSt.execute();
            }
            mysqlRs.close();
            mysqlSt.close();
            insertSt.close();
        }
    }

    public static void main(String[] args)
    {
        try
        {
            for (City c : Cities)
                addByTable.addCity(c);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
