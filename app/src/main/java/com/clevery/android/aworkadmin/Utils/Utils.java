package com.clevery.android.aworkadmin.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static String getFormattedTimeStr(int miliseconds) {
        String str = "";
        str = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(miliseconds), TimeUnit.MILLISECONDS.toMinutes(miliseconds),
                TimeUnit.MILLISECONDS.toSeconds(miliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(miliseconds)));
        return str;
    }

}
