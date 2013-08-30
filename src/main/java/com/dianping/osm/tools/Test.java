package com.dianping.osm.tools;
import java.io.*;

public class Test
{
    public static long f(int x)
    {
        long m = 0;
        long n0 = 1;
        long n1 = 0;
        long n2 = 0;
        long n3 = 0;
        long r = 0;
        for (int i = 0; i<x; ++i)
        {
            m += n3;
            n3 = n2;
            n2 = n1;
            n1 = n0;
            n0 = m;
            r = m+n0+n1+n2+n3;
        }
        return r;
    }

    public static void main(String args[])
    {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line;
            int n = 0;
            while ((line = in.readLine()) != null)
            {
                try
                {
                    n = Integer.parseInt(line.trim());
                    System.out.println(f(n));
                }
                catch (NumberFormatException e) {
                    continue;
                }
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}