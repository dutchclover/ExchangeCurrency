package com.dgroup.exchangerates.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeFormatter {

    public static final long SECOND = 1000L;
    public static final long TWO_SECOND = 2L * SECOND;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long MINUTE = SECONDS_PER_MINUTE * SECOND;
    public static final long PULL_TO_REFRESH_PAUSE = 30L * SECOND;
    public static final long HOUR = MINUTE * 60L;
    public static final long HALF_OF_DAY = 12L * HOUR;
    public static final long DAY = 24L * HOUR;
    public static final long MONTH = 30L * DAY;

    private static TimeFormatter instance = null;

    private SimpleDateFormat apiCBDateFormat;
    private SimpleDateFormat servDateFormat;

    private TimeFormatter(){
        apiCBDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        servDateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm", Locale.getDefault());
    }

    public static TimeFormatter getInstance() {
        if (instance == null) {
            instance = new TimeFormatter();
        }
        return instance;
    }

    public String getApiCBDate(long timestamp){
        return apiCBDateFormat.format(timestamp);
    }

    public long getLongServTime(String servTime){
        try{
            return servDateFormat.parse(servTime).getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public String getStringServTime(long time){
        return servDateFormat.format(new Date(time));
    }
}
