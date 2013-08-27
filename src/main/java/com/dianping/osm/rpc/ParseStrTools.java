package com.dianping.osm.rpc;

import java.io.StringReader;
import java.util.List;

import org.xml.sax.InputSource;

public class ParseStrTools {

    public static String parseLogicLocationFromXmlStr(String xmlStr) {
        // String[] pairs = xmlStr.split("=");
        boolean isIn = false;
        xmlStr = xmlStr.replaceAll("</li>", "#");
        StringBuffer sb = new StringBuffer(xmlStr);
        // System.out.println("sb:"+sb.toString());
        for (int i = 0; i < sb.length(); i++) {
            if (isIn) {
                char c = sb.charAt(i);
                // System.out.println(c);
                if (c == '>') {
                    isIn = false;
                    sb.deleteCharAt(i);
                    i--;
                    if (i + 1 < sb.length() && sb.charAt(i + 1) != '<' && sb.charAt(i + 1) != '\n') {
                        sb.insert(i + 1, ';');
                    }
                    continue;
                } else {
                    sb.deleteCharAt(i);
                    i--;
                }
            } else {
                char c = sb.charAt(i);
                // System.out.println(c);
                if (c == '<') {
                    isIn = true;
                    sb.deleteCharAt(i);
                    i--;
                }
            }

        }

        String str = "";
        String[] strArray = sb.toString().split("#");
        boolean isValid = false;
        for (int i = 0; i < strArray.length; i++) {
            // System.out.println("-----"+i+":"+"-----");

            String[] str1 = strArray[i].split(";");
            if (str1.length > 2) {
                str += i + ":";
                str += str1[1] + "," + str1[2] + "\r\n";
                isValid = true;
            }
            // for(int j=1;j<str1.length-1;j++)
            // //System.out.println(j+":"+str1[j]);
            // str+=str1[j]
        }

        return str;
    }

    public static String parseLogicLocationFromXmlStr_google(String xmlStr) {
        int startIndex = 0, endIndex = 0;
        int i = xmlStr.indexOf("laddr");
        int num = 0;
        while (num < 2) {
            if (xmlStr.charAt(i) == '"') {
                num++;
                if (num == 1) startIndex = i+1;
                else if (num == 2) endIndex = i;

            }
            i++;
        }

        return xmlStr.substring(startIndex,endIndex);
    }


}
