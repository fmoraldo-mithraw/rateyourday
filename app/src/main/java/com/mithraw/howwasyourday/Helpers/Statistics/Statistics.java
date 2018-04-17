package com.mithraw.howwasyourday.Helpers.Statistics;

/*
A simple class that reflect the stats
Used for days, month and year time by filling the good data
Used in Funny Stats
 */
public class Statistics {


    StatisticsAdds statisticsAdds = null;
    StatisticsDatas favoriteDay = null;
    StatisticsDatas worstDay = null;
    StatisticsDatas favoriteMonth = null;
    StatisticsDatas worstMonth = null;
    StatisticsDatas favoriteYear = null;
    StatisticsDatas worstYear = null;
    public StatisticsAdds getStatisticsAdds() {
        return statisticsAdds;
    }

    public Statistics setStatisticsAdds(StatisticsAdds statisticsAdds) {
        this.statisticsAdds = statisticsAdds;
        return this;
    }

    public StatisticsDatas getFavoriteDay() {
        return favoriteDay;
    }

    public Statistics setFavoriteDay(StatisticsDatas favoriteDay) {
        this.favoriteDay = favoriteDay;
        return this;
    }

    public StatisticsDatas getWorstDay() {
        return worstDay;
    }

    public Statistics setWorstDay(StatisticsDatas worstDay) {
        this.worstDay = worstDay;
        return this;
    }

    public StatisticsDatas getFavoriteMonth() {
        return favoriteMonth;
    }

    public Statistics setFavoriteMonth(StatisticsDatas favoriteMonth) {
        this.favoriteMonth = favoriteMonth;
        return this;
    }

    public StatisticsDatas getWorstMonth() {
        return worstMonth;
    }

    public Statistics setWorstMonth(StatisticsDatas worstMonth) {
        this.worstMonth = worstMonth;
        return this;
    }

    public StatisticsDatas getFavoriteYear() {
        return favoriteYear;
    }

    public Statistics setFavoriteYear(StatisticsDatas favoriteYear) {
        this.favoriteYear = favoriteYear;
        return this;
    }

    public StatisticsDatas getWorstYear() {
        return worstYear;
    }

    public Statistics setWorstYear(StatisticsDatas worstYear) {
        this.worstYear = worstYear;
        return this;
    }

    public Statistics() {
    }

}