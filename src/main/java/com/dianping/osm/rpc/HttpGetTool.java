package com.dianping.osm.rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class HttpGetTool {

	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlName = url + "?" + param;
			URL realUrl = new URL(urlName);
			// �򿪺�URL֮�������
			URLConnection conn = realUrl.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Accept_Language", "zh-cn");
			conn.setRequestProperty("Accept_Encoding", "gzip,deflate");
			conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			conn.setRequestProperty("Accept", "text/html, */*; q=0.01");
	
				
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			//httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); 

			// ����ʵ�ʵ�����
			conn.connect();
			// ��ȡ������Ӧͷ�ֶ�
			Map<String, List<String>> map = conn.getHeaderFields();
			// �������е���Ӧͷ�ֶ�
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}

			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(),Charset.forName("utf-8")));
			String line;
			while ((line = in.readLine()) != null) {
				result += "\n" + line;
			}
		} catch (Exception e) {
			System.out.println("����GET��������쳣��" + e);
			e.printStackTrace();
		}
		// ʹ��finally�����ر�������
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		

		return result;
	}
	
	public static String sendGet_osm(String url, double la,double lo) {
		String result = "";
		BufferedReader in = null;
		String param="query="+la+"%2C"+lo+"%5C";
		try {
			String urlName = url + "?" + param;
			URL realUrl = new URL(urlName);
			// �򿪺�URL֮�������
			URLConnection conn = realUrl.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Accept_Language", "zh-cn");
			conn.setRequestProperty("Accept_Encoding", "gzip,deflate");
			conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			conn.setRequestProperty("Accept", "text/html, */*; q=0.01");
	
				
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			//httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); 

			// ����ʵ�ʵ�����
			conn.connect();
			// ��ȡ������Ӧͷ�ֶ�
			Map<String, List<String>> map = conn.getHeaderFields();
			// �������е���Ӧͷ�ֶ�
			
//			for (String key : map.keySet()) {
//				System.out.println(key + "--->" + map.get(key));
//			}

			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(),Charset.forName("utf-8")));
			String line;
			while ((line = in.readLine()) != null) {
				result += "\n" + line;
			}
		} catch (Exception e) {
		//	System.out.println("����GET��������쳣��" + e);
			e.printStackTrace();
		}
		// ʹ��finally�����ر�������
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

    public static String sendGet_google(String url, double la,double lo) {
        String result = "";
        BufferedReader in = null;
        String param="output=js&q="+la+"%2C"+lo;
        try {
            String urlName = url + "?" + param;
           // System.out.println("url"+urlName);
            URL realUrl = new URL(urlName);
            // �򿪺�URL֮�������
            URLConnection conn = realUrl.openConnection();
            // ����ͨ�õ���������
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Accept_Language", "zh-cn");
            conn.setRequestProperty("Accept_Encoding", "gzip,deflate");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.setRequestProperty("Accept", "text/html, */*; q=0.01");


            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            //httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            // ����ʵ�ʵ�����
            conn.connect();
            // ��ȡ������Ӧͷ�ֶ�
            Map<String, List<String>> map = conn.getHeaderFields();
            // �������е���Ӧͷ�ֶ�

//			for (String key : map.keySet()) {
//				System.out.println(key + "--->" + map.get(key));
//			}

            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),Charset.forName("utf-8")));
            String line;
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }
        } catch (Exception e) {
            //	System.out.println("����GET��������쳣��" + e);
            e.printStackTrace();
        }
        // ʹ��finally�����ر�������
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

}
