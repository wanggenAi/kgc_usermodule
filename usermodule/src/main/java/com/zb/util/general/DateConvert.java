package com.zb.util.general;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConvert {
    public static String longToDateTime(long lo) {
        Date date = new Date(lo);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static String longToDate(long lo) {
        Date date = new Date(lo);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}
