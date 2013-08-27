package com.dianping.osm.tools;


import com.dianping.geo.map.entity.GeoPoint;
import com.dianping.osm.rpc.ProvinceName;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoordinateToAddress {
    private static DBConnector connector = new DBConnector();
    private static long number = 0;

    public static void SetDatabaseDriverName(String drivename) {
        connector.setDiverName(drivename);
    }

    public static void SetDatabaseUrl(String url) {
        connector.setUrl(url);
    }

    public static void SetDatabaseUser(String user) {
        connector.setUser(user);
    }

    public static void SetDatabasePassword(String password) {
        connector.setPassword(password);
    }

    public static void clusterByProvince() {
        System.out.println("start!");

        int min = 0, max = 10000;

        while (min < 12686600) {

            try {
                Class.forName("org.postgresql.Driver").newInstance();
                String url = "jdbc:postgresql://localhost:5432/china_gis";
                Connection con = DriverManager.getConnection(url, "postgres", "ws");
                Statement st = con.createStatement();
                clusterByProvince_1(min, max, st);
                min += 10000;
                //max += 10000;
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

//        try {
//            Class.forName("org.postgresql.Driver").newInstance();
//            String url = "jdbc:postgresql://localhost:5432/china_gis";
//            Connection con = DriverManager.getConnection(url, "postgres", "ws");
//            Statement st = con.createStatement();
//            String sql = "SELECT ST_X(geom::geometry),ST_Y(geom::geometry),id  FROM nodes limit 5 offset 0;";
//            ResultSet rs = st.executeQuery(sql);
//
//            CoordinateToAddress t = new CoordinateToAddress();
//            t.SetDatabaseUrl("jdbc:postgresql://localhost:5432/boundary");
//            t.SetDatabaseUser("postgres");
//            t.SetDatabasePassword("ws");
//            ProvinceName.inti();
//            List<String[]> retList = new ArrayList<String[]>();
//            while (rs.next()) {
//                System.out.print(rs.getString(1) + " ");
//                System.out.print(rs.getString(2) + " ");
//                System.out.println(rs.getString(3) + "");
//
//                String[] strArray = {rs.getString(1), rs.getString(2), rs.getString(3)};
//
//                retList.add(strArray);
//
//
//            }
//
//            for (int i = 0; i < retList.size(); i++) {
//
//                String lo = retList.get(i)[0];
//                String la = retList.get(i)[1];
//                String id = retList.get(i)[2];
//
//                GeoPoint point = new GeoPoint(Double.parseDouble(la), Double.parseDouble(lo));
//                String provinceName = ProvinceName.provinceName.get(t.ProvinceName(point));
//
//
//                try {
//                    sql = "SELECT * INTO nodes_" + provinceName + " FROM nodes WHERE  1=0;";
//                    st.executeQuery(sql);
//                } catch (Exception e) {
//                }
//
//
//                sql = "INSERT INTO nodes_" + provinceName + "  SELECT * FROM nodes WHERE  id=" + id + ";";
//                st.execute(sql);
//
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
        System.out.println("end!");

    }

    public static void clusterByProvince_1(int min, int max, Statement st) {
        try {

            String sql = "SELECT ST_X(geom::geometry),ST_Y(geom::geometry),id  FROM nodes limit " + max + " offset " + min + ";";
            ResultSet rs = st.executeQuery(sql);

            CoordinateToAddress t = new CoordinateToAddress();
            t.SetDatabaseUrl("jdbc:postgresql://localhost:5432/boundary");
            t.SetDatabaseUser("postgres");
            t.SetDatabasePassword("ws");
            ProvinceName.inti();
            List<String[]> retList = new ArrayList<String[]>();
            while (rs.next()) {
//                System.out.print(rs.getString(1) + " ");
//                System.out.print(rs.getString(2) + " ");
//                System.out.println(rs.getString(3) + "");
                String[] strArray = {rs.getString(1), rs.getString(2), rs.getString(3)};
                retList.add(strArray);
            }

            for (int i = 0; i < retList.size(); i++) {

                String lo = retList.get(i)[0];
                String la = retList.get(i)[1];
                String id = retList.get(i)[2];

                GeoPoint point = new GeoPoint(Double.parseDouble(la), Double.parseDouble(lo));
                String provinceName = ProvinceName.provinceName.get(t.ProvinceName(point));


                try {
                    sql = "SELECT * INTO nodes_" + provinceName + " FROM nodes WHERE  1=0;";
                    st.executeQuery(sql);
                } catch (Exception e) {
                }


                sql = "INSERT INTO nodes_" + provinceName + "  SELECT * FROM nodes WHERE  id=" + id + ";";
                st.execute(sql);
                number++;
                float r=(((float)number)/(float)12686500)*100;
                if((number%100)==0||number>12686500)System.out.println(number+": "+r+"%");
            }


        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }


    public static String ProvinceName(GeoPoint point) {

        SetDatabaseUrl("jdbc:postgresql://localhost:5432/chinaboundary");
        SetDatabaseUser("postgres");
        SetDatabasePassword("ws");
        double lng = point.getLng();
        double lat = point.getLat();
        String pStr = lng + " " + lat;
        try {
            Connection con = connector.getConnection();
            Statement st = con.createStatement();
            String sql = "SELECT region_name FROM province WHERE ST_WITHIN(ST_GeomFromText('POINT("
                    + pStr
                    + ")',4326),region_boundary) order by region_id DESC;";
            ResultSet rs = st.executeQuery(sql);
            String result;
            if (rs.next())
                result = rs.getString(1);
            else
                result = "Not Found!";
            rs.close();
            st.close();
            con.close();
            return result;
        } catch (Exception e) {
            return e.toString();
        }
    }

    public static void main(String[] args) {


//        CoordinateToAddress t = new CoordinateToAddress();
//        t.SetDatabaseUrl("jdbc:postgresql://localhost:5432/chinaboundary");
//        t.SetDatabaseUser("postgres");
//        t.SetDatabasePassword("ws");
        GeoPoint point = new GeoPoint(41.966696800000001,121.5964403);
        System.out.println(ProvinceName(point));


       // t.clusterByProvince();
        System.out.println("end!");
    }
}
