package com.mithraw.howwasyourday.Helpers.Statistics;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
Format the day rate in the FunnyStat Screen
 */
public class DayHelper extends StatisticsHelper  {
    static private DayHelper mDayHelper = null;

    private DayHelper() {
    }

    static public DayHelper getInstance() {
        if (mDayHelper == null)
            mDayHelper = new DayHelper();
        return mDayHelper;
    }

    public String format(StatisticsDatas day, int precision) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, day.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        java.util.Date d = new java.util.Date(calendar.getTimeInMillis());
        return finalFormat(sdf.format(d), day.getRate(), precision);
    }
}
