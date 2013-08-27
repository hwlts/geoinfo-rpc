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
 * Date: 13-8-15
 * Time: 上午10:30
 * To change this template use File | Settings | File Templates.
 */
public class DBTableCluster {
    private static long number=0;



    public static void main(String[] args){
        GeoPoint p=new GeoPoint(31,121);
        //judgeIndex(p);
        //System.out.print(judgeIndex(p));
        clusterByMatrix();
    }


    public static void clusterByMatrix() {
        System.out.println("start!");

        int offset = 0, limit = 10000;

        while (offset < 1300000) {

            try {
                Class.forName("org.postgresql.Driver").newInstance();
                String url = "jdbc:postgresql://localhost:5432/china_gis";
                Connection con = DriverManager.getConnection(url, "postgres", "ws");
                Statement st = con.createStatement();
                clusterByMatrix_37(offset, limit, st);
                offset += 10000;
                //max += 10000;
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

        System.out.println("end!");

    }

    public static void clusterByMatrix_1(long offset, int limit, Statement st) {
        try {

            String sql = "SELECT ST_X(geom::geometry),ST_Y(geom::geometry),id  FROM nodes limit " + limit + " offset " + offset + ";";
            ResultSet rs = st.executeQuery(sql);

//            CoordinateToAddress t = new CoordinateToAddress();
//            t.SetDatabaseUrl("jdbc:postgresql://localhost:5432/boundary");
//            t.SetDatabaseUser("postgres");
//            t.SetDatabasePassword("ws");
//            ProvinceName.inti();
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
                int index=judgeIndex(point);

               if(index>=0){
                try {
                    sql = "SELECT * INTO nodes100_" + index + " FROM nodes WHERE  1=0;";
                    st.executeQuery(sql);
                } catch (Exception e) {
                }

                sql = "INSERT INTO nodes100_" + index + "  SELECT * FROM nodes WHERE  id=" + id + ";";
                st.execute(sql);
                number++;
                float r=(((float)number)/(float)12686500)*100;
                if((number%100)==0||number>12686500)System.out.println(number+": "+r+"%");
               }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }



    public static void clusterByMatrix_16(long offset, int limit, Statement st) {
        try {

            String sql = "SELECT ST_X(geom::geometry),ST_Y(geom::geometry),id  FROM nodes100_16 limit " + limit + " offset " + offset + ";";
            ResultSet rs = st.executeQuery(sql);


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
                int index=judgeIndex_16(point);

                if(index>=0){
                    try {
                        sql = "SELECT * INTO nodes100_16_" + index + " FROM nodes WHERE  1=0;";
                        st.executeQuery(sql);
                    } catch (Exception e) {
                    }

                    sql = "INSERT INTO nodes100_16_" + index + "  SELECT * FROM nodes WHERE  id=" + id + ";";
                    st.execute(sql);
                    number++;
                    float r=(((float)number)/(float)1700000)*100;
                    if((number%100)==0||number>1700000)System.out.println(number+": "+r+"%");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void clusterByMatrix_37(long offset, int limit, Statement st) {
        try {

            String sql = "SELECT ST_X(geom::geometry),ST_Y(geom::geometry),id  FROM nodes100_37 limit " + limit + " offset " + offset + ";";
            ResultSet rs = st.executeQuery(sql);


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
                int index=judgeIndex_37(point);

                if(index>=0){
                    try {
                        sql = "SELECT * INTO nodes100_37_" + index + " FROM nodes WHERE  1=0;";
                        st.executeQuery(sql);
                    } catch (Exception e) {
                    }

                    sql = "INSERT INTO nodes100_37_" + index + "  SELECT * FROM nodes WHERE  id=" + id + ";";
                    st.execute(sql);
                    number++;
                    float r=(((float)number)/(float)1300000)*100;
                    if((number%100)==0||number>1300000)System.out.println(number+": "+r+"%");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static int judgeIndex(GeoPoint p){
        int i=-1,j=-1;

        if(p.getLat()<=54&&p.getLat()>=18&&p.getLng()<=135.2&&p.getLng()>=72){
            i=(int)((p.getLat()-18)/3.6);
            j=(int)((p.getLng()-72)/6.4);
            return i*10+j;
        }
        else return -1;


    }


    public static int judgeIndex_16(GeoPoint p){
        int i=-1,j=-1;
        if(p.getLat()<=25.6&&p.getLat()>=21.6&&p.getLng()<=116.8&&p.getLng()>=110.4){
            i=(int)((p.getLat()-21.6)/1.8);
            j=(int)((p.getLng()-110.4)/3.2);
            return i*2+j;
        }
       else return -1;
    }

    public static int judgeIndex_37(GeoPoint p){
        int i=-1,j=-1;
        if(p.getLat()<=32.4&&p.getLat()>=28.8&&p.getLng()<=123.2&&p.getLng()>=116.8){
            i=(int)((p.getLat()-28.8)/1.8);
            j=(int)((p.getLng()-116.8)/3.2);
            return i*2+j;
        }
        else return -1;
    }




}
