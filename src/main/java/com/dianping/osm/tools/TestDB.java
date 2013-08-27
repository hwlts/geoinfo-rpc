package com.dianping.osm.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class TestDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// System.out.print( " this is a test " );

		//searchByPoint_1(31.2974449, 121.5008946, 200);
		
		searchByPoint_1(31.2033986, 121.4305421, 200);

	}

	public static void searchByPoint(double la, double lo, double distance) {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			String url = "jdbc:postgresql://localhost:5432/postgis20";
			Connection con = DriverManager.getConnection(url,"postgres","admin");
			Statement st = con.createStatement();

			// select from way_tags table
			String sql = "SELECT way_id,v FROM way_tags where k='name' and way_id IN(SELECT way_id FROM way_nodes where node_id IN(SELECT id FROM nodes where ST_distance_sphere(nodes.geom::geometry,'POINT("
					+ lo
					+ " "
					+ la
					+ ")')<"
					+ distance
					+ " ORDER BY ST_distance_sphere(nodes.geom::geometry,'POINT("
					+ lo + " " + la + ")')ASC));";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				System.out.print(rs.getString(1) + ",");
				System.out.println(rs.getString(2));
				// System.out.println(rs.getString(3));
			}

			System.out.println("-----------------------");

			// select from node_tags table
			sql = "SELECT node_id,node_tags.v FROM node_tags where node_tags.k='name' and node_tags.node_id IN(SELECT id FROM nodes where ST_distance_sphere(nodes.geom::geometry,'POINT("
					+ lo + " " + la + ")')<" + distance + ") ;";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				System.out.print(rs.getString(1) + ",");
				System.out.println(rs.getString(2));
				// System.out.println(rs.getString(3));
			}

			rs.close();
			st.close();
			con.close();

		} catch (Exception ee) {
			System.out.print(ee.getMessage());
		}

	}

	public static void searchByPoint_1(double la, double lo, double distance) {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			String url = "jdbc:postgresql://localhost:5432/postgis20";
			Connection con = DriverManager.getConnection(url, "postgres", "admin");
			Statement st = con.createStatement();

			// select from way_tags table
			String sql = "SELECT id FROM nodes  where ST_distance_sphere(nodes.geom::geometry,'POINT("
					+ lo
					+ " "
					+ la
					+ ")')<"
					+ distance
					+ " ORDER BY ST_distance_sphere(nodes.geom::geometry,'POINT("
					+ lo + " " + la + ")');";
			ResultSet rs = st.executeQuery(sql);
			List<String> node_id_list = new ArrayList<String>();
			while (rs.next()) {
				node_id_list.add(rs.getString(1));
				// System.out.println(rs.getString(1));
			}
			rs.close();

			Set<String> way_id_set = new HashSet<String>();
			for (int i = 0; i < node_id_list.size(); i++) {
				String sql22 = "SELECT  way_id FROM way_nodes where node_id="
						+ node_id_list.get(i);
				ResultSet rs22 = st.executeQuery(sql22);
				List<String> way_id_list = new ArrayList<String>();
				while (rs22.next()) {
					String way_id = rs22.getString(1);
					if (!way_id_set.contains(way_id)) {
						way_id_list.add(way_id);
						way_id_set.add(way_id);
					}

				}

				for(int j=0;j<way_id_list.size();j++){
					String sql2 = "SELECT way_id,v FROM way_tags where k='name' and way_id ="+way_id_list.get(j);
					ResultSet rs2 = st.executeQuery(sql2);
					while (rs2.next())
                    {
						System.out.print(rs2.getString(1) + ",");
						System.out.println(rs2.getString(2));
					}
					
				}



			}

			System.out.println("-----------------------");

			for (int i = 0; i < node_id_list.size(); i++) {
				// System.out.println(""+i+":");
				String sq3 = "SELECT node_id,node_tags.v FROM node_tags where node_tags.k='name' and node_tags.node_id ="
						+ node_id_list.get(i) + " ;";
				ResultSet rs3 = st.executeQuery(sq3);
				while (rs3.next()) {
					System.out.print(rs3.getString(1) + ",");
					System.out.println(rs3.getString(2));
					// System.out.println(rs.getString(3));
				}
			}

			// sql =
			// "SELECT node_id,node_tags.v FROM node_tags where node_tags.k='name' and node_tags.node_id IN(SELECT id FROM nodes where ST_distance_sphere(nodes.geom::geometry,'POINT("+lo+" "+la+")')<"+distance+") ;";
			// rs = st.executeQuery(sql);
			// while (rs.next()) {
			// System.out.print(rs.getString(1) + ",");
			// System.out.println(rs.getString(2));
			// // System.out.println(rs.getString(3));
			// }

			rs.close();
			st.close();
			con.close();

		} catch (Exception ee) {
			System.out.print(ee.getMessage());
		}

	}

}
