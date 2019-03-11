package com.example.herem1t.rc_client.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

    public static String MsToDate(long ms) {
        String dateFormat = "yy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        Date date = new Date(ms);
        return simpleDateFormat.format(date);
    }


    public static long stringToDateSec(String date){
        long dateSec = 0;
        SimpleDateFormat simpleDF = new SimpleDateFormat("yy-MM-dd HH:mm");
        try {
            Date mDate = simpleDF.parse(date);
            dateSec = (long)mDate.getTime()/1000;
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        return dateSec;
    }


}
