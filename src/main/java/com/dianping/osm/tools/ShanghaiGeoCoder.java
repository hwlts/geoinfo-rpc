package com.dianping.osm.tools;

import com.dianping.geo.map.entity.GeoPoint;

import java.sql.*;
import java.util.*;

public class ShanghaiGeoCoder
{
    public static void main(String[] args)
    {

    }

    public void Address(GeoPoint toCodePoint)
    {
        double lo = toCodePoint.getLng();
        double la = toCodePoint.getLat();
        double acc_dis = 50.0;
        try
        {
            DBConnector connector = new DBConnector();
            connector.setDiverName("org.postgresql.Driver");
            connector.setUrl("jdbc:postgresql://localhost:5432/postgis20");
            connector.setUser("postgres");
            connector.setPassword("admin");
            Connection con = connector.getConnection();

            try
            {
                //search nodes which has a less distance than acc_dis from toCodePoint in nodes_TABLE
                List<String> node_id_list = new ArrayList<String>();
                Statement st = con.createStatement();
                String sql1 = "SELECT id FROM nodes  where ST_distance_sphere(nodes.geom::geometry,'POINT("
                             + lo
                             + " "
                             + la
                             + ")')<"
                             + acc_dis
                             + " ORDER BY ST_distance_sphere(nodes.geom::geometry,'POINT("
                             + lo + " " + la + ")');";
                ResultSet rs1 = st.executeQuery(sql1);
                while (!rs1.next())
                {
                    acc_dis += 50.0;
                    sql1 = "SELECT id FROM nodes  where ST_distance_sphere(nodes.geom::geometry,'POINT("
                            + lo
                            + " "
                            + la
                            + ")')<"
                            + acc_dis
                            + " ORDER BY ST_distance_sphere(nodes.geom::geometry,'POINT("
                            + lo + " " + la + ")');";
                    rs1 = st.executeQuery(sql1);
                }
                while (rs1.next())
                {
                    node_id_list.add(rs1.getString(1));
                }
                rs1.close();

                //search node names in node_tags_TABLE
                for (int i = 0; i < node_id_list.size(); i++)
                {
                    String sql2 = "SELECT node_id,node_tags.v FROM node_tags where node_tags.k='name' and node_tags.node_id ="
                                 + node_id_list.get(i) + " ;";
                    ResultSet rs2 = st.executeQuery(sql2);
                    while (rs2.next())
                    {
                        System.out.print(rs2.getString(1) + ",");
                        System.out.println(rs2.getString(2));
                    }
                    rs2.close();
                }

                System.out.println("-----------------------");


                Set<String> way_id_set = new HashSet<String>();
                Set<String> way_name_set = new HashSet<String>();
                for (int i = 0; i < node_id_list.size(); i++)
                {
                    //search ways which include nodes obtained from the previous step
                    String sql3 = "SELECT  way_id FROM way_nodes where node_id="
                                   + node_id_list.get(i);
                    ResultSet rs3 = st.executeQuery(sql3);
                    List<String> way_id_list = new ArrayList<String>();
                    while (rs3.next())
                    {
                        String way_id = rs3.getString(1);
                        if (!way_id_set.contains(way_id))
                        {
                            way_id_list.add(way_id);
                            way_id_set.add(way_id);
                        }
                    }
                    rs3.close();

                    //search way names in way_tags_TABLE
                    for(int j = 0;j < way_id_list.size();j++)
                    {
                        String sql4 = "SELECT way_id,v FROM way_tags where k='name' and way_id ="+way_id_list.get(j);
                        ResultSet rs4 = st.executeQuery(sql4);
                        while (rs4.next())
                        {
                            String way_name = rs4.getString(2);
                            if (!way_name_set.contains(way_name))
                            {
                                System.out.print(rs4.getString(1) + ",");
                                System.out.println(rs4.getString(2));
                            }
                        }
                    }
                }
            }
            catch(Exception e)
            {}
        }
        catch (Exception e)
        {}
    }
}
