package com.dianping.osm.uitls;

import com.dianping.geo.map.entity.GeoPoint;
import com.dianping.osm.tools.CoordinateToAddress;
import com.dianping.osm.rpc.ProvinceName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shan.wu
 * Date: 13-8-14
 * Time: 下午4:07
 * To change this template use File | Settings | File Templates.
 */
public class AddDBIndexTool {
    private static long number = 0;

    public static  void main(String[] args){
        //addProvince();
                 //24323778

        try {
            Class.forName("org.postgresql.Driver").newInstance();
            String url = "jdbc:postgresql://localhost:5432/china_gis";
            Connection con = DriverManager.getConnection(url, "postgres", "ws");
            Statement st = con.createStatement();
            String sql = "SELECT ST_X(geom::geometry),ST_Y(geom::geometry),id  FROM nodes WHERE id=24323778;";
            ResultSet rs = st.executeQuery(sql);

            if(rs.next()){
            CoordinateToAddress t = new CoordinateToAddress();
            t.SetDatabaseUrl("jdbc:postgresql://localhost:5432/boundary");
            t.SetDatabaseUser("postgres");
            t.SetDatabasePassword("ws");
            ProvinceName.inti();

            GeoPoint point = new GeoPoint(Double.parseDouble(rs.getString(2)), Double.parseDouble(rs.getString(1)));
            String provinceName = ProvinceName.provinceName.get(t.ProvinceName(point));

            System.out.println("la:"+rs.getString(2)+"la:"+rs.getString(1)+provinceName) ;

                try {
                    sql = "UPDATE nodes SET province = \'"+provinceName+"\' WHERE id = "+rs.getString(3)+";";
                    st.execute(sql);
                } catch (Exception e) {
                }

            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }



    public static void addProvince() {

        System.out.println("start!");

        int min = 0, max = 10000;

        while (min < 12686600) {

            try {
                Class.forName("org.postgresql.Driver").newInstance();
                String url = "jdbc:postgresql://localhost:5432/china_gis";
                Connection con = DriverManager.getConnection(url, "postgres", "ws");
                Statement st = con.createStatement();
                addProvince_1(min, max, st);
                min += 10000;
                max += 10000;
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }

    public static void addProvince_1(int min, int max, Statement st) {
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
                    sql = "UPDATE nodes SET province = "+provinceName+" WHERE id = "+id+";";
                    st.executeQuery(sql);
                } catch (Exception e) {
                }

                number++;
                float r=(((float)number)/(float)12686500)*100;
                if((number%100)==0||number>12686500)System.out.println(number+": "+r+"%");
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
