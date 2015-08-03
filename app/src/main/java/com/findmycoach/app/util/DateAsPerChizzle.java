package com.findmycoach.app.util;

/**
 * Created by ved on 3/8/15.
 */
public class DateAsPerChizzle {
    public static String YYYY_MM_DD_into_DD_MM_YYYY(String YYYY_MM_DD){
        /*
        * Here date coming for conversion is in format yyyy-mm-dd
        * */
        String formatted_date=null;
         try {

            if (YYYY_MM_DD != null && YYYY_MM_DD.trim() != "") {
                String [] date=YYYY_MM_DD.split("-");
                formatted_date=String.format("%02d-%02d-%d",Integer.parseInt(date[2]),Integer.parseInt(date[1]),Integer.parseInt(date[0]));
                return formatted_date;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return formatted_date;

    }

}
