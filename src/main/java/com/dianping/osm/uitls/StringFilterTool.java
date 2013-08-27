package com.dianping.osm.uitls;

/**
 * Created with IntelliJ IDEA.
 * User: shan.wu
 * Date: 13-8-20
 * Time: 下午2:59
 * To change this template use File | Settings | File Templates.
 */
public class StringFilterTool {

    public static void main(String[] args)
    {
        System.out.println(filterAlphabet("德   fwef胜 门内f  w大 街fwef"));
    }


    public static String filterAlphabet(String input) {
        StringBuffer output = new StringBuffer(input);

        for(int i=0;i<output.length();i++)
        {
            if(isLetter(output.charAt(i))||output.charAt(i)==' ')
            {
                output.deleteCharAt(i);
                i--;
            }
        }

        return output.toString();
    }




    public static boolean isLetter(char c)

    {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[a-zA-Z]+");
        String str=c+"";
        java.util.regex.Matcher m = pattern.matcher(str);
        return m.matches();
    }


}
