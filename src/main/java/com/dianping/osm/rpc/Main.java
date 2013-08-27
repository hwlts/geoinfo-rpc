package com.dianping.osm.rpc;

import com.dianping.geo.map.CoordTransferService;
import com.dianping.geo.map.entity.CoordType;
import com.dianping.geo.map.entity.GeoPoint;
import com.dianping.osm.tools.CoordinateToAddress;
import com.dianping.osm.uitls.DBTableCluster;
import com.dianping.osm.uitls.StringFilterTool;
import com.dianping.osm.uitls.TestTool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: shan.wu
 * Date: 13-8-9
 * Time: 下午1:52
 * To change this template use File | Settings | File Templates.
 */
public class Main extends JFrame implements ActionListener {
    private static Set<String> way_id_set = new HashSet<String>();
    private Container container;
    private JTextArea resultText_dp;
    private JTextArea resultText_osm;
    private JTextArea resultText_google_grap;
    private JTextArea resultText_dp_log;
    private JTextField tf_la_lo;
    private JButton bt_search;
    private JButton bt_startTest;
    private JButton bt_nextCase;
    private JButton bt_autoTest;
    private JButton bt_pause;
    private JLabel label_testResult;
    private JComboBox bb_city;
    private static CoordTransferService coordTransferService;
    //private final static String dbName = "postgis20";
    private final static String dbName = "china_gis";
    private static ResultSet testCaseRs;
    private static boolean pause = false;

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath*:config/spring/common/appcontext-*.xml", "classpath*:config/spring/local/appcontext-*.xml"});
        coordTransferService = (CoordTransferService) context.getBean("CoordTransferService");
        new Main();


        GeoPoint p = new GeoPoint(31.43679, 120.35651);
        //GeoPoint p = new GeoPoint(31.2984, 121.5035);

        googleToGps(p);

        //searchByPoint_Google(31.20154,121.43517);
    }

    public Main() {
        System.out.println("init");
        super.setTitle("Map Tools Of DP");
        this.setSize(600, 300);
        container = this.getContentPane();
        container.setLayout(new BorderLayout());

        // dp results text
        JPanel centerPanel_dp = new JPanel();
        JScrollPane scrollPanel_dp = new JScrollPane();
        centerPanel_dp.setLayout(new BoxLayout(centerPanel_dp, BoxLayout.Y_AXIS));
        resultText_dp = new JTextArea();
        resultText_dp.setEditable(false);
        scrollPanel_dp.setViewportView(resultText_dp);
        centerPanel_dp.add(new JLabel("DP"));
        centerPanel_dp.add(scrollPanel_dp);

        //osm results text
        JPanel centerPanel_osm = new JPanel();
        JScrollPane scrollPanel_osm = new JScrollPane();
        centerPanel_osm.setLayout(new BoxLayout(centerPanel_osm, BoxLayout.Y_AXIS));
        resultText_osm = new JTextArea();
        resultText_osm.setEditable(false);
        scrollPanel_osm.setViewportView(resultText_osm);
        centerPanel_osm.add(new JLabel("OSM"));
        centerPanel_osm.add(scrollPanel_osm);

        //google grap results text
        JPanel centerPanel_google_grap = new JPanel();
        JScrollPane scrollPanel_google_grap = new JScrollPane();
        centerPanel_google_grap.setLayout(new BoxLayout(centerPanel_google_grap, BoxLayout.Y_AXIS));
        resultText_google_grap = new JTextArea();
        resultText_google_grap.setEditable(false);
        scrollPanel_google_grap.setViewportView(resultText_google_grap);
        centerPanel_google_grap.add(new JLabel("Grab From Google "));
        centerPanel_google_grap.add(scrollPanel_google_grap);

        // google
        JPanel centerPanel_google = new JPanel();
        JScrollPane scrollPanel_google = new JScrollPane();
        centerPanel_google.setLayout(new BoxLayout(centerPanel_google, BoxLayout.Y_AXIS));
        resultText_dp_log = new JTextArea();
        resultText_dp_log.setEditable(false);
        scrollPanel_google.setViewportView(resultText_dp_log);
        centerPanel_google.add(new JLabel("DP Log"));
        centerPanel_google.add(scrollPanel_google);


        container.add(centerPanel_dp, BorderLayout.WEST);
        container.add(centerPanel_osm, BorderLayout.CENTER);
        container.add(centerPanel_google_grap, BorderLayout.EAST);
        container.add(centerPanel_google, BorderLayout.NORTH);


        // bottom button &input frame
        JPanel bottomPanel = new JPanel();
        tf_la_lo = new JTextField(20);
        bt_search = new JButton("查找");
        bt_search.setActionCommand("search");
        bt_startTest = new JButton("生成测试用例");
        bt_startTest.setActionCommand("startTest");
        bt_nextCase = new JButton("下一个");
        bt_nextCase.setActionCommand("nextCase");
        bt_autoTest = new JButton("自动测试");
        bt_autoTest.setActionCommand("autoTest");
        bt_pause = new JButton("暂停");
        bt_pause.setActionCommand("pause");
        bb_city = new JComboBox();
        bb_city.setActionCommand("selectCity");
        bb_city.setModel(new DefaultComboBoxModel(new String[]{"上海", "天津", "宁波", "扬州", "无锡", "北京", "西安", "武汉", "杭州", "广州", "南京", "苏州", "深圳", "成都", "重庆"}));
        bottomPanel.add(new JLabel("纬度,经度："));
        bottomPanel.add(tf_la_lo);
        bottomPanel.add(bt_search);
        //bottomPanel.add(bt_startTest);
        bottomPanel.add(bb_city);
        bottomPanel.add(bt_nextCase);
        bottomPanel.add(bt_autoTest);
        bottomPanel.add(bt_pause);
        label_testResult = new JLabel("匹配个数/总个数");
        bottomPanel.add(label_testResult);


        container.add(bottomPanel, BorderLayout.SOUTH);
        container.validate();

        // register Listener
        tf_la_lo.addActionListener(this);
        bt_search.addActionListener(this);
        bt_startTest.addActionListener(this);
        bt_nextCase.addActionListener(this);
        bt_autoTest.addActionListener(this);
        bb_city.addActionListener(this);
        bt_pause.addActionListener(this);
        this.setVisible(true);
    }

    public static GeoPoint gpsToGoogle(GeoPoint gpsPoint) {
        GeoPoint offset1 = coordTransferService.coordTranfer(gpsPoint,
                CoordType.GPS, CoordType.GOOGLE);
        //System.out.println("gpsToGoogle la:" + offset1.getLat() + " lo:" + offset1.getLng());
        return offset1;
    }

    public static GeoPoint googleToGps(GeoPoint googlePoint) {
        //GeoPoint origin1 = new GeoPoint(la, lo);
        GeoPoint offset1 = coordTransferService.coordTranfer(googlePoint,
                CoordType.GOOGLE, CoordType.GPS);
        System.out.println("la:" + offset1.getLat() + " lo:" + offset1.getLng());
        return offset1;
    }

    public static void searchByPoint(double la, double lo, double distance) {

        try {
            Class.forName("org.postgresql.Driver").newInstance();
            String url = "jdbc:postgresql://localhost:5432/postgis20";
            Connection con = DriverManager.getConnection(url, "postgres", "ws");
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

    public static void searchByPoint_3(double la, double lo) {
        final double inc = 500;
        int down = 0;
        int up = 500;
        while (!searchByPoint_1(la, lo, down, up)) {
            down += inc;
            up += inc;
        }
    }

    public static boolean searchByPoint_1(double la, double lo, double down,
                                          double up) {
        boolean haveResults = false;
        System.out.println("============" + down + "~" + up
                + "米范围内============");
        try {
            Class.forName("org.postgresql.Driver").newInstance();
            String url = "jdbc:postgresql://localhost:5432/postgis20";
            Connection con = DriverManager.getConnection(url, "postgres", "ws");
            Statement st = con.createStatement();

            // select from way_tags table
            String sql = "SELECT id FROM nodes  where"
                    + " ST_distance_sphere(nodes.geom::geometry,'POINT("
                    + lo
                    + " "
                    + la
                    + ")')>="
                    + down
                    + " and "
                    + "ST_distance_sphere(nodes.geom::geometry,'POINT("
                    + lo
                    + " "
                    + la
                    + ")')<"
                    + up
                    + " ORDER BY ST_distance_sphere(nodes.geom::geometry,'POINT("
                    + lo + " " + la + ")');";
            ResultSet rs = st.executeQuery(sql);
            ArrayList<String> node_id_list = new ArrayList<String>();
            while (rs.next()) {
                node_id_list.add(rs.getString(1));
                // System.out.println(rs.getString(1));
            }
            rs.close();

            // Set<String> way_id_set = new HashSet<String>();
            for (int i = 0; i < node_id_list.size(); i++) {
                String sql22 = "SELECT  way_id FROM way_nodes where node_id="
                        + node_id_list.get(i);
                ResultSet rs22 = st.executeQuery(sql22);
                ArrayList<String> way_id_list = new ArrayList<String>();
                while (rs22.next()) {
                    String way_id = rs22.getString(1);
                    if (!way_id_set.contains(way_id)) {
                        way_id_list.add(way_id);
                        way_id_set.add(way_id);
                    }

                }

                for (int j = 0; j < way_id_list.size(); j++) {
                    String sql2 = "SELECT way_id,v FROM way_tags where k='name' and way_id ="
                            + way_id_list.get(j);
                    ResultSet rs2 = st.executeQuery(sql2);
                    while (rs2.next()) {
                        System.out.print(rs2.getString(1) + ",");
                        System.out.println(rs2.getString(2));
                        haveResults = true;
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
                    haveResults = true;
                    // System.out.println(rs.getString(3));
                }

            }

            rs.close();
            st.close();
            con.close();

        } catch (Exception ee) {
            System.out.print(ee.getMessage());
        }
        return haveResults;
    }

    public boolean searchByPoint_2(double la, double lo, double down, double up, String tableIndex) {
        boolean haveResults = false;
        // System.out.println("============" + down + "~" + up
        // + "米范围内============");
        //resultText_dp.append("   DP GIS Server   \n");
        resultText_dp.append("============" + down + "~" + up
                + "米范围内============\n");
        GeoPoint p = new GeoPoint(la, lo);
        String superLocation = CoordinateToAddress.ProvinceName(p);

        try {
            Class.forName("org.postgresql.Driver").newInstance();
            String url = "jdbc:postgresql://localhost:5432/" + dbName;
            Connection con = DriverManager.getConnection(url, "postgres", "ws");
            Statement st = con.createStatement();

            // select from way_tags table
            String sql = "SELECT id," + "ST_distance_sphere(geom::geometry,'POINT("
                    + lo + " " + la + ")')," + "ST_X(geom::geometry),ST_Y(geom::geometry)" + " FROM nodes100_" + tableIndex + "  where"
                    + " ST_distance_sphere(geom::geometry,'POINT("
                    + lo
                    + " "
                    + la
                    + ")')>="
                    + down
                    + " and "
                    + "ST_distance_sphere(geom::geometry,'POINT("
                    + lo
                    + " "
                    + la
                    + ")')<"
                    + up
                    + " ORDER BY ST_distance_sphere(geom::geometry,'POINT("
                    + lo + " " + la + ")');";


            //String sql="SELECT id from nodes_"+tableIndex+"  limit 10";

            ResultSet rs = st.executeQuery(sql);
            ArrayList<String[]> node_id_list = new ArrayList<String[]>();
            // ArrayList<String> node_dis_lsit= new ArrayList<String> ();
            while (rs.next()) {
                String[] strArray = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)};

                node_id_list.add(strArray);

            }
            rs.close();
            resultText_dp.append("找点结束" + "\n");

            // Set<String> way_id_set = new HashSet<String>();
            Set<String> way_tag_set = new HashSet<String>();
            for (int i = 0; i < node_id_list.size(); i++) {
                String sql22 = "SELECT  way_id FROM way_nodes where node_id="
                        + node_id_list.get(i)[0];
                ResultSet rs22 = st.executeQuery(sql22);
                ArrayList<String[]> way_id_list = new ArrayList<String[]>();
                while (rs22.next()) {
                    String way_id = rs22.getString(1);
                    if (!way_id_set.contains(way_id)) {
                        String[] strArray = {way_id, node_id_list.get(i)[1], node_id_list.get(i)[2], node_id_list.get(i)[3]};
                        way_id_list.add(strArray);
                        way_id_set.add(way_id);
                    }

                }


                for (int j = 0; j < way_id_list.size(); j++) {
                    String sql2 = "SELECT way_id,v FROM way_tags where k='name' and way_id ="
                            + way_id_list.get(j)[0];
                    ResultSet rs2 = st.executeQuery(sql2);

                    while (rs2.next()) {
                        // System.out.print(rs2.getString(1) + ",");
                        // System.out.println(rs2.getString(2));
                        String way_tag = rs2.getString(2);
                        if (!way_tag_set.contains(way_tag)) {

                            GeoPoint p1 = new GeoPoint(Double.parseDouble(way_id_list.get(j)[3]), Double.parseDouble(way_id_list.get(j)[2]));
                            GeoPoint p2 = new GeoPoint(la, lo);
                            String distance = way_id_list.get(j)[1];


//                            resultText_dp.append(rs2.getString(1) + ","
//                                    + rs2.getString(2) + " "+judgeDirection(p1,p2)+" "+Float.parseFloat(distance)+"米\n");

                            String location = StringFilterTool.filterAlphabet(rs2.getString(2));
                            if (location.length() > 1) {
                                resultText_dp.append(superLocation + " " + location + " " + judgeDirection(p1, p2) + " " + Float.parseFloat(distance) + "米\n");
                                way_tag_set.add(way_tag);
                                haveResults = true;
                            }
                        }


                    }

                }

            }

            resultText_dp.append("-----------------------\n");

            for (int i = 0; i < node_id_list.size(); i++) {
                // System.out.println(""+i+":");
                String sq3 = "SELECT node_id,node_tags.v FROM node_tags where node_tags.k='name' and node_tags.node_id ="
                        + node_id_list.get(i)[0] + " ;";
                ResultSet rs3 = st.executeQuery(sq3);

                while (rs3.next()) {
                    // System.out.print(rs3.getString(1) + ",");
                    // System.out.println(rs3.getString(2));

                    GeoPoint p1 = new GeoPoint(Double.parseDouble(node_id_list.get(i)[3]), Double.parseDouble(node_id_list.get(i)[2]));
                    GeoPoint p2 = new GeoPoint(la, lo);
                    String distance = node_id_list.get(i)[1];
//                    resultText_dp.append(rs3.getString(1) + "," + rs3.getString(2)
//                             + " "+judgeDirection(p1,p2)+" "+Float.parseFloat(distance)+"米\n");
                    String location = StringFilterTool.filterAlphabet(rs3.getString(2));
                    if (location.length() > 1) {
                        resultText_dp.append(superLocation + " " + location
                                + " " + judgeDirection(p1, p2) + " " + Float.parseFloat(distance) + "米\n");
                        haveResults = true;
                    }

                    // System.out.println(rs.getString(3));
                }

            }

            rs.close();
            st.close();
            con.close();

        } catch (Exception ee) {
            System.out.print(ee.getMessage());
        }
        return haveResults;
    }


    public ArrayList<String> searchByPoint_dp_test(double la, double lo, double down, double up, String tableIndex) {
        ArrayList<String> result = new ArrayList<String>();
        way_id_set.clear();
        resultText_dp.setText("");
        resultText_dp.append("============" + down + "~" + up
                + "米范围内============\n");
        GeoPoint p = new GeoPoint(la, lo);
        String superLocation = CoordinateToAddress.ProvinceName(p);

        try {
            Class.forName("org.postgresql.Driver").newInstance();
            String url = "jdbc:postgresql://localhost:5432/" + dbName;
            Connection con = DriverManager.getConnection(url, "postgres", "ws");
            Statement st = con.createStatement();

            // select from way_tags table
            String sql = "SELECT id," + "ST_distance_sphere(geom::geometry,'POINT("
                    + lo + " " + la + ")')," + "ST_X(geom::geometry),ST_Y(geom::geometry)" + " FROM nodes100_" + tableIndex + "  where"
                    + " ST_distance_sphere(geom::geometry,'POINT("
                    + lo
                    + " "
                    + la
                    + ")')>="
                    + down
                    + " and "
                    + "ST_distance_sphere(geom::geometry,'POINT("
                    + lo
                    + " "
                    + la
                    + ")')<"
                    + up
                    + " ORDER BY ST_distance_sphere(geom::geometry,'POINT("
                    + lo + " " + la + ")');";


            //String sql="SELECT id from nodes_"+tableIndex+"  limit 10";

            ResultSet rs = st.executeQuery(sql);
            ArrayList<String[]> node_id_list = new ArrayList<String[]>();
            // ArrayList<String> node_dis_lsit= new ArrayList<String> ();
            while (rs.next()) {
                String[] strArray = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)};

                node_id_list.add(strArray);

            }
            rs.close();
            resultText_dp.append("找点结束" + "\n");

            // Set<String> way_id_set = new HashSet<String>();
            int number = 0;

            Set<String> way_tag_set = new HashSet<String>();
            for (int i = 0; i < node_id_list.size(); i++) {
                String sql22 = "SELECT  way_id FROM way_nodes where node_id="
                        + node_id_list.get(i)[0];
                ResultSet rs22 = st.executeQuery(sql22);
                ArrayList<String[]> way_id_list = new ArrayList<String[]>();
                while (rs22.next()) {
                    String way_id = rs22.getString(1);
                    if (!way_id_set.contains(way_id)) {
                        String[] strArray = {way_id, node_id_list.get(i)[1], node_id_list.get(i)[2], node_id_list.get(i)[3]};
                        way_id_list.add(strArray);
                        way_id_set.add(way_id);
                    }

                }


                for (int j = 0; j < way_id_list.size(); j++) {
                    String sql2 = "SELECT way_id,v FROM way_tags where k='name' and way_id ="
                            + way_id_list.get(j)[0];
                    ResultSet rs2 = st.executeQuery(sql2);

                    while (rs2.next()) {
                        // System.out.print(rs2.getString(1) + ",");
                        // System.out.println(rs2.getString(2));
                        String way_tag = rs2.getString(2);
                        if (!way_tag_set.contains(way_tag)) {

                            GeoPoint p1 = new GeoPoint(Double.parseDouble(way_id_list.get(j)[3]), Double.parseDouble(way_id_list.get(j)[2]));
                            GeoPoint p2 = new GeoPoint(la, lo);
                            String distance = way_id_list.get(j)[1];
                            String location = StringFilterTool.filterAlphabet(rs2.getString(2));
                            if (location.length() > 1) {
                                if (number < 10) {
                                    resultText_dp.append(superLocation + " " + location + " " + judgeDirection(p1, p2) + " " + Float.parseFloat(distance) + "米\n");
                                    number++;
                                }
                                way_tag_set.add(way_tag);
                                result.add(location);
                            }
                        }
                    }
                }
            }


            rs.close();
            st.close();
            con.close();

        } catch (Exception ee) {
            System.out.print(ee.getMessage());
        }
        return result;
    }


    private String judgeDirection(GeoPoint p1, GeoPoint p2) {
        String lo = "";
        if (p2.getLng() - p1.getLng() > 0.00002) lo += "东";
        else if (p2.getLng() - p1.getLng() < -0.00002) lo += "西";

        String la = "";
        if (p2.getLat() - p1.getLat() > 0.00002) la += "北";
        else if (p2.getLat() - p1.getLat() < -0.00002) la += "南";
        return lo + la;
    }

    public void searchByPoint_DP(double la, double lo, double max) {
        way_id_set.clear();
        resultText_dp.setText("");
        final double inc = 1000;
        int down = 0;
        int up = 1000;

        GeoPoint p = new GeoPoint(la, lo);
        String table_tag = DBTableCluster.judgeIndex(p) + "";
        if (table_tag.equals("16")) table_tag += "_" + DBTableCluster.judgeIndex_16(p);
        else if (table_tag.equals("37")) table_tag += "_" + DBTableCluster.judgeIndex_37(p);

        System.out.println("index=" + table_tag);
        //searchByPoint_2(la, lo, down, up, table_tag);
        while (!searchByPoint_2(la, lo, down, up, table_tag) || (up < max)) {
            down += inc;
            up += inc;
        }
        this.validate();
    }

    public void searchByPoint_OSM(double la, double lo) {
        String ret = HttpGetTool.sendGet_osm(
                "http://www.openstreetmap.org/geocoder/search_osm_nominatim",
                la, lo);
        // System.out.println("ret:"+ret);
        String parsedStr = ParseStrTools.parseLogicLocationFromXmlStr(ret);
        resultText_osm.setText(parsedStr);
        this.validate();
        //System.out.println("parsedStr:"+parsedStr);
    }

    public String searchByPoint_Google(double la, double lo) {
        GeoPoint gpsPoint = new GeoPoint(la, lo);
        GeoPoint googelPoint = gpsToGoogle(gpsPoint);


        String ret = HttpGetTool.sendGet_google(
                "http://ditu.google.cn/maps",
                googelPoint.getLat(), googelPoint.getLng());
        //System.out.println("ret:" + ret);
        String parsedStr = ParseStrTools.parseLogicLocationFromXmlStr_google(ret);
        //System.out.println("parsedStr:"+parsedStr);

        resultText_google_grap.setText(parsedStr);
        return parsedStr;
    }

    public void searchByPoint_All(final double la, final double lo) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                searchByPoint_DP(la,
                        lo, 1000);
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                searchByPoint_OSM(la,
                        lo);
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                searchByPoint_Google(la,
                        lo);
            }
        }.start();
        this.validate();
    }


    public boolean searchByPoint_Test(final double la, final double lo, String dp_log) {


        GeoPoint p = new GeoPoint(la, lo);
        String table_tag = DBTableCluster.judgeIndex(p) + "";
        if (table_tag.equals("16")) table_tag += "_" + DBTableCluster.judgeIndex_16(p);
        else if (table_tag.equals("37")) table_tag += "_" + DBTableCluster.judgeIndex_37(p);
        System.out.println("start dp search...");
        ArrayList<String> dpList = searchByPoint_dp_test(la, lo, 0, 5000, table_tag);
        System.out.println(" dp search finished!");
        System.out.println("start google grab...");
        String google = searchByPoint_Google(la,
                lo);
        System.out.println("google grab finished!.");
        this.validate();

        for (int i = 0; i < dpList.size() && i <= 10; i++) {
            String dp = dpList.get(i);
//            if (google.contains(dp)) {
//                return true;
//            }

            if (isMatch(dp, google, dp_log)) {
                return true;
            }

        }
        return false;
    }

    public static boolean isMatch(String dp, String google, String dp_log) {
        for (int i = 0; i < dp.length(); i++)
            for (int j = i + 3; j < dp.length() + 1; j++) {
                if (google.contains(dp.substring(i, j)) || dp_log.contains(dp.substring(i, j))) {
                    System.out.println("match sub string:" + dp.substring(i, j));

                    //if(dp.substring(i, j).equals("北京"))pause=true;
                    return true;
                }


            }


        return false;
    }


    public void autoTest() {
        final double[] number = {0};
        final double[] matchNumber = {0};


        new Thread() {
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                try {
                    while (testCaseRs.next()) {
                        String la = testCaseRs.getString(1);
                        String lo = testCaseRs.getString(2);

                        String address = testCaseRs.getString(3) + " " + testCaseRs.getString(4) + " " + testCaseRs.getString(5);

                        GeoPoint p1 = new GeoPoint(Double.parseDouble(la), Double.parseDouble(lo));
                        GeoPoint p2 = gpsToGoogle(p1);


                        tf_la_lo.setText(p2.getLat() + "," + p2.getLng());
                        resultText_dp_log.setText(address);


                        if (searchByPoint_Test(Double.parseDouble(la), Double.parseDouble(lo), address))
                            matchNumber[0]++;
                        number[0]++;
                        label_testResult.setText((int) matchNumber[0] + "/" + (int) number[0]);
                        Main.this.validate();
                        // System.out.println("match/total:"+matchNumber[0]+"/"+number[0]);
                        long i = 0;
                        System.out.println(pause);
                        while (pause) {
//                           i++;
//                            if(i==1000000000)pause=false;
                            System.out.println(pause);
                            sleep(100);
                        }
                    }
                    label_testResult.setText("match ratio: " + (matchNumber[0] / number[0] * 100) + "%");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();


        // return matchNumber[0] / number[0];
    }

    public void startTest() {
        testCaseRs = TestTool.creatTestCase();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

        System.out.println(e.getActionCommand());
        if (e.getActionCommand().equals("search")) {
            String la_lo_str = tf_la_lo.getText();
            String[] laAndLo = la_lo_str.split(",");
            final String la = laAndLo[0];
            final String lo = laAndLo[1];

            if (lo != null && !lo.equals("") && la != null
                    && !la.equals("")) {
//			searchByPoint_DP(Double.parseDouble(la),
//					Double.parseDouble(lo),200);
                searchByPoint_All(Double.parseDouble(la), Double.parseDouble(lo));
                this.validate();
            }
        } else if (e.getActionCommand().equals("startTest")) {
            startTest();
        } else if (e.getActionCommand().equals("nextCase")) {

            bt_nextCase.setEnabled(false);
            try {
                if (testCaseRs.next()) {
                    String la = testCaseRs.getString(1);
                    String lo = testCaseRs.getString(2);
                    searchByPoint_All(Double.parseDouble(la), Double.parseDouble(lo));
                    String address = testCaseRs.getString(3) + " " + testCaseRs.getString(4) + " " + testCaseRs.getString(5);

                    GeoPoint p1 = new GeoPoint(Double.parseDouble(la), Double.parseDouble(lo));
                    GeoPoint p2 = gpsToGoogle(p1);


                    tf_la_lo.setText(p2.getLat() + "," + p2.getLng());
                    resultText_dp_log.setText(address);

                    this.validate();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            bt_nextCase.setEnabled(true);

        } else if (e.getActionCommand().equals("selectCity")) {
            System.out.println("selectCity");
            TestTool.selectCity(bb_city.getSelectedItem());
            testCaseRs = TestTool.creatTestCase();

        } else if (e.getActionCommand().equals("autoTest")) {

            autoTest();

        } else if (e.getActionCommand().equals("pause")) {
            if (!pause) {
                pause = true;
                bt_pause.setText("继续");
            } else {
                pause = false;
                bt_pause.setText("暂停");
            }

            System.out.println(pause);
        }


    }
}
