package com.dianping.osm.tools;

import com.dianping.geo.map.entity.GeoPoint;
import com.dianping.geo.map.CoordTransferService;
import com.dianping.geo.map.entity.CoordType;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.*;


public class BoundaryFileToDB
{
    private DBConnector connector;
    private String BoundaryFilePath;
    private CoordTransferService coordTransferService;

    public BoundaryFileToDB()
    {
        connector = new DBConnector();
        BoundaryFilePath = "";
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:config/spring/common/appcontext-*.xml", "classpath*:config/spring/local/appcontext-*.xml"});
        coordTransferService = (CoordTransferService) context.getBean("coordTransferService");
    }

    public void SetDatabaseDriverName(String drivename)
    {
        connector.setDiverName(drivename);
    }

    public void SetDatabaseUrl(String url)
    {
        connector.setUrl(url);
    }

    public void SetDatabaseUser(String user)
    {
        connector.setUser(user);
    }

    public void SetDatabasePassword(String password)
    {
        connector.setPassword(password);
    }

    public void SetFilePath(String path)
    {
        BoundaryFilePath = path;
    }


    public void Insert()
    {
        try
        {
            Connection con = connector.getConnection();

            File file=new File(BoundaryFilePath);
            if(file.isFile() && file.exists())
            {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file),"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;
                long region_id = 1;
                while((lineTxt = bufferedReader.readLine()) != null)
                {
                    if (lineTxt.contains(";"))
                    {
                        String[] StrArr = lineTxt.split(":");
                        String region_nameStr = "'"+StrArr[0]+"'";
                        String[] PointStrAtr = StrArr[1].split(";");
                        System.out.println(StrArr[0]);
                        //System.out.println(PointStrAtr.length);
                        String region_boundaryStr = "";
                        String region_idStr = region_id+"";
                        region_id++;
                        String parent_idStr;
                        if(StrArr[0].equals("中国"))
                        {
                            long parent_id = 0;
                            parent_idStr = parent_id+"";
                        }
                        else
                        {
                            long parent_id = 1;
                            parent_idStr = parent_id+"";
                        }
                        for(String pointStr:PointStrAtr)
                        {
                            String[] tempArr = pointStr.split(",");
                            double lng = Double.parseDouble(tempArr[0]);
                            double lat = Double.parseDouble(tempArr[1]);
                            GeoPoint baiduPoint = new GeoPoint(lat,lng);
                            GeoPoint gpsPoint = coordTransferService.coordTranfer(baiduPoint, CoordType.BAIDU, CoordType.GPS);
                            String temp = gpsPoint.getLng()+" "+gpsPoint.getLat();
                            if(region_boundaryStr.length()==0)
                                region_boundaryStr += temp;
                            else
                            {
                                String temp1 = ","+temp;
                                region_boundaryStr += temp1;
                            }
                        }
                        //System.out.println(region_nameStr);
                        //System.out.println(region_boundaryStr);

                        String sql = "INSERT INTO test_boundaries(region_id,parent_id,region_name,region_boundary) values("
                                +region_idStr
                                +","
                                +parent_idStr
                                +","
                                +region_nameStr
                                +",ST_Polygon(ST_GeomFromText('LINESTRING("
                                +region_boundaryStr
                                +")'),4326));";
                        //System.out.println(sql);
                        PreparedStatement st = con.prepareStatement(sql);
                        st.execute();
                        st.close();
                    }
                }
                read.close();
            }
            else
            {
                System.out.println("Can not find file.");
            }
            con.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void TransferService()
    {
        GeoPoint bP = new GeoPoint(39.435315,119.455116);
        GeoPoint gP = coordTransferService.coordTranfer(bP,CoordType.BAIDU,CoordType.GPS);
        System.out.print(gP.getLat());
        System.out.print(",");
        System.out.print(gP.getLng());
    }

    public static void main(String[] args)
    {
        BoundaryFileToDB t = new BoundaryFileToDB();
        t.SetDatabaseUrl("jdbc:postgresql://192.168.32.208:5432/boundary");
        t.SetDatabaseUser("postgres");
        t.SetDatabasePassword("ws");
        t.SetFilePath("E:\\ChinaProvinceBoundary");
        t.Insert();
    }
}
