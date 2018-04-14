package com.mithraw.howwasyourday.Helpers.Statistics;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
Format the rate of the month in FunnyStats
 */
public class MonthHelper extends StatisticsHelper {
    static private MonthHelper mMonthHelper = null;

    private MonthHelper() {
    }

    static public MonthHelper getInstance() {
        if (mMonthHelper == null)
            mMonthHelper = new MonthHelper();
        return mMonthHelper;
    }

    public String format(StatisticsDatas month, int precision) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
        java.util.Date d = new java.util.Date(calendar.getTimeInMillis());
        return finalFormat(sdf.format(d), month.getRate(), precision);
    }
}
