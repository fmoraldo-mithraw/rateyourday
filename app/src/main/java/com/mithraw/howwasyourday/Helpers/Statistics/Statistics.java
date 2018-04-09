package com.mithraw.howwasyourday.Helpers.Statistics;

public class Statistics {

    StatisticsDatas favoriteDay = null;
    StatisticsDatas worstDay = null;
    StatisticsDatas favoriteMonth = null;
    StatisticsDatas worstMonth = null;
    StatisticsDatas favoriteYear = null;
    StatisticsDatas worstYear = null;

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