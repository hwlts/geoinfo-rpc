package com.dianping.osm.uitls;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: shan.wu
 * Date: 13-8-15
 * Time: 上午11:31
 * To change this template use File | Settings | File Templates.
 */
public class TestTool {
    private static Connection conn = null;
    private static PreparedStatement statement = null;
    //private static final String tableName ="CI_HotCity_10";//shanghai
    private static String tableName = "CI_HotCity_100";//tianjin


    public static void main(String[] args) {
        creatTestCase();

    }

    public static void selectCity(Object city) {
        String c=(String)city;
        if (c.equals("上海")) tableName = "CI_HotCity_10";
        else if (c.equals("天津")) tableName = "CI_HotCity_100";
        else if (c.equals("宁波")) tableName = "CI_HotCity_110";
        else if (c.equals("扬州")) tableName = "CI_HotCity_120";
        else if (c.equals("无锡")) tableName = "CI_HotCity_130";

        else if (c.equals("武汉")) tableName = "CI_HotCity_160";
        else if (c.equals("西安")) tableName = "CI_HotCity_170";
        else if (c.equals("北京")) tableName = "CI_HotCity_20";
        else if (c.equals("杭州")) tableName = "CI_HotCity_30";

        else if (c.equals("广州")) tableName = "CI_HotCity_40";
        else if (c.equals("南京")) tableName = "CI_HotCity_50";
        else if (c.equals("苏州")) tableName = "CI_HotCity_60";
        else if (c.equals("深圳")) tableName = "CI_HotCity_70";

        else if (c.equals("成都")) tableName = "CI_HotCity_80";
        else if (c.equals("重庆")) tableName = "CI_HotCity_90";
    }

    public static ResultSet creatTestCase() {
        String url = "jdbc:mysql://192.168.8.44:3306/DianPingMap?characterEncoding=UTF-8";
        String username = "dpcom_map";
        String password = "dp!@tempuser"; // 加载驱动程序以连接数据库
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
        }
        //捕获加载驱动程序异常
        catch (ClassNotFoundException cnfex) {
            System.err.println(
                    "装载 JDBC/ODBC 驱动程序失败。");
            cnfex.printStackTrace();
        }
        //捕获连接数据库异常
        catch (SQLException sqlex) {
            System.err.println("无法连接数据库");
            sqlex.printStackTrace();
        }


        String sql = "SELECT * FROM DianPingMap." + tableName + " ORDER BY rand() LIMIT 1000;";
        ResultSet rs = selectSQL(sql);

//        try {
//            if(rs.next()) {
//
//                System.out.print(rs.getString(1)+" ");
//                System.out.print(rs.getString(2)+" ");
//                System.out.print(rs.getString(3)+" ");
//                System.out.print(rs.getString(4)+" ");
//                System.out.println(rs.getString(5) + " ");
//
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
        return rs;
    }

    public static ResultSet selectSQL(String sql) {
        ResultSet rs = null;
        try {
            statement = conn.prepareStatement(sql);
            rs = statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

}
