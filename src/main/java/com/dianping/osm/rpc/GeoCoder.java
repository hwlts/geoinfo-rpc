package com.dianping.osm.rpc;

import com.dianping.geo.map.entity.GeoPoint;
import com.dianping.osm.sql.PostgreSQLCon;

import java.sql.*;

/*DP_RGC_Service*/

public class GeoCoder
{
    class AddAcc
    {
        String address = "";
        double accuracy = 1000.0;

        public AddAcc(String s, double d)
        {
            address = s;
            accuracy = d;
        }
    }


    private static String SearchInRecord(GeoPoint point)
    {
        PostgreSQLCon postgreSQLCon =  new PostgreSQLCon();
        postgreSQLCon.setUrl("jdbc:postgresql://192.168.32.100:5432/test1");
        postgreSQLCon.setUsername("postgres");
        postgreSQLCon.setPassword("ws");
        Connection con = postgreSQLCon.getConnection();
        try
        {
            Statement st = con.createStatement();
            double lng = point.getLng();
            double lat = point.getLat();
            double lngMin = lng-0.0001;
            double latMin = lat-0.0001;

            String sql = ""
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }
}
