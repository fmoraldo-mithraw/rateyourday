package com.mithraw.howwasyourday.Helpers.Statistics;

public class YearHelper extends StatisticsHelper  {
    static private YearHelper mYearHelper = null;

    private YearHelper() {
    }

    static public YearHelper getInstance() {
        if (mYearHelper == null)
            mYearHelper = new YearHelper();
        return mYearHelper;
    }

    public String format(StatisticsDatas year, int precision) {
        return finalFormat(Integer.toString(year.getId()), year.getRate(), precision);
    }
}
